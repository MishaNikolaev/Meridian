package com.nmichail.meridian.features.calls.catalog.data.media

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RendererCommon
import org.webrtc.RtpReceiver
import org.webrtc.RtpTransceiver
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import org.webrtc.audio.JavaAudioDeviceModule

class WebRtcCall(context: Context, video: Boolean, loud: Boolean) {
	private val main = Handler(Looper.getMainLooper())
	private val egl = EglBase.create()
	private val audioRoute = CallAudioRoute(context)
	private val factory: PeerConnectionFactory
	private val audioDevice: JavaAudioDeviceModule
	private val audioSource: AudioSource
	private val localAudio: AudioTrack
	private val capturer: org.webrtc.CameraVideoCapturer?
	private val texture: SurfaceTextureHelper?
	private val videoSource: VideoSource?
	private val localVideo: VideoTrack?
	private var remoteVideo: VideoTrack? = null
	private var remoteView: SurfaceViewRenderer? = null
	private val queued = ArrayDeque<String>()
	private val negotiation: WebRtcNegotiation
	private val pc: PeerConnection
	private var stopped = false
	private val outgoing = ArrayDeque<String>()
	var onSignal: ((String) -> Unit)? = null
		set(value) {
			field = value
			if (value != null) {
				while (outgoing.isNotEmpty()) {
					value(outgoing.removeFirst())
				}
			}
		}

	private val media = MutableStateFlow(CallMediaStatus(muted = false, speaker = loud, remoteVideo = false))
	val mediaStatus = media.asStateFlow()

	private val constraints = MediaConstraints().apply {
		mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
		if (video) {
			mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
		}
	}

	init {
		val attributes = AudioAttributes.Builder()
			.setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
			.setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
			.build()
		audioRoute.start(loud, attributes)
		PeerConnectionFactory.initialize(
			PeerConnectionFactory.InitializationOptions.builder(context)
				.createInitializationOptions(),
		)
		audioDevice = JavaAudioDeviceModule.builder(context)
			.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
			.setAudioAttributes(attributes)
			.setUseHardwareAcousticEchoCanceler(true)
			.setUseHardwareNoiseSuppressor(true)
			.createAudioDeviceModule()
		factory = PeerConnectionFactory.builder()
			.setAudioDeviceModule(audioDevice)
			.setVideoEncoderFactory(
				DefaultVideoEncoderFactory(egl.eglBaseContext, true, true),
			)
			.setVideoDecoderFactory(DefaultVideoDecoderFactory(egl.eglBaseContext))
			.createPeerConnectionFactory()
		audioSource = factory.createAudioSource(MediaConstraints())
		localAudio = factory.createAudioTrack("audio", audioSource)
		localAudio.setEnabled(true)
		val cameras = Camera2Enumerator(context)
		val cameraName = cameras.deviceNames
			.firstOrNull { name -> cameras.isFrontFacing(name) }
			?: cameras.deviceNames.firstOrNull()
		capturer = if (video) cameraName?.let { cameras.createCapturer(it, null) } else null
		texture = capturer?.let {
			SurfaceTextureHelper.create("meridian-call", egl.eglBaseContext)
		}
		videoSource = capturer?.let { factory.createVideoSource(false) }
		localVideo = videoSource?.let { source ->
			checkNotNull(capturer).initialize(
				texture,
				context,
				source.capturerObserver,
			)
			capturer.startCallCapture()
			factory.createVideoTrack("local", source)
		}
		val config = PeerConnection.RTCConfiguration(
			listOf(
				PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
				PeerConnection.IceServer.builder("stun:stun.cloudflare.com:3478").createIceServer(),
			),
		)
		config.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
		config.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
		config.enableIceGatheringOnAnyAddressPorts = true
		config.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
		pc = checkNotNull(factory.createPeerConnection(config, peer()))
		negotiation = WebRtcNegotiation(
			pc = pc,
			constraints = constraints,
			dispatch = ::dispatch,
			emitSignal = ::emitSignal,
		)
		pc.setAudioPlayout(true)
		pc.setAudioRecording(true)
		pc.addTrack(localAudio, listOf("call"))
		localVideo?.let { track ->
			track.setEnabled(true)
			pc.addTrack(track, listOf("call"))
		}
	}

	fun setMuted(on: Boolean) {
		if (stopped || media.value.muted == on) {
			return
		}
		localAudio.setEnabled(!on)
		pc.setAudioRecording(!on)
		media.value = media.value.copy(muted = on)
	}

	fun setSpeaker(on: Boolean) {
		if (stopped || media.value.speaker == on) {
			return
		}
		audioRoute.selectSpeaker(on)
		media.value = media.value.copy(speaker = on)
	}

	fun show(renderer: SurfaceViewRenderer, remote: Boolean) {
		if (stopped) {
			return
		}
		renderer.init(egl.eglBaseContext, null)
		renderer.setMirror(!remote)
		renderer.setZOrderMediaOverlay(!remote)
		renderer.setEnableHardwareScaler(true)
		renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
		if (remote) {
			remoteView = renderer
			remoteVideo?.addSink(renderer)
		} else {
			localVideo?.addSink(renderer)
		}
	}

	fun hide(renderer: SurfaceViewRenderer) {
		if (remoteView === renderer) {
			remoteView = null
		}
		try {
			localVideo?.removeSink(renderer)
		} catch (_: RuntimeException) {
			Unit
		}
		try {
			remoteVideo?.removeSink(renderer)
		} catch (_: RuntimeException) {
			Unit
		}
		try {
			renderer.release()
		} catch (_: RuntimeException) {
			Unit
		}
	}

	fun offer() {
		if (!stopped) negotiation.offer()
	}

	fun take(body: String) {
		if (!stopped) negotiation.take(body)
	}

	fun stop() {
		if (stopped) {
			return
		}
		stopped = true
		onSignal = null
		outgoing.clear()
		try {
			capturer?.stopCapture()
		} catch (_: RuntimeException) {
			Unit
		}
		capturer?.dispose()
		try {
			pc.dispose()
		} catch (_: RuntimeException) {
			Unit
		}
		localVideo?.dispose()
		localAudio.dispose()
		videoSource?.dispose()
		audioSource.dispose()
		texture?.dispose()
		factory.dispose()
		audioDevice.release()
		egl.release()
		audioRoute.stop()
	}

	private fun dispatch(action: () -> Unit) {
		main.post { if (!stopped) action() }
	}

	private fun emitSignal(body: String) {
		dispatch {
			val listener = onSignal
			if (listener == null) outgoing.addLast(body) else listener(body)
		}
	}

	private fun peer() = object : PeerConnection.Observer {
		override fun onSignalingChange(state: PeerConnection.SignalingState) = Unit
		override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
			val connected = state == PeerConnection.IceConnectionState.CONNECTED || state == PeerConnection.IceConnectionState.COMPLETED
			if (!connected) {
				return
			}
			dispatch {
				audioRoute.selectSpeaker(media.value.speaker)
				pc.setAudioPlayout(true)
				pc.setAudioRecording(!media.value.muted)
			}
		}
		override fun onIceConnectionReceivingChange(receiving: Boolean) = Unit
		override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) = Unit
		override fun onIceCandidate(candidate: IceCandidate) {
			emitSignal("ice\n${candidate.sdpMid.orEmpty()}\n${candidate.sdpMLineIndex}\n${candidate.sdp}")
		}
		override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) = Unit
		override fun onAddStream(stream: org.webrtc.MediaStream) = Unit
		override fun onRemoveStream(stream: org.webrtc.MediaStream) = Unit
		override fun onDataChannel(channel: org.webrtc.DataChannel) = Unit
		override fun onRenegotiationNeeded() = Unit
		override fun onAddTrack(receiver: RtpReceiver, streams: Array<out org.webrtc.MediaStream>) = Unit
		override fun onTrack(transceiver: RtpTransceiver) {
			when (val track = transceiver.receiver.track()) {
				is AudioTrack -> {
					dispatch {
						track.setEnabled(true)
						pc.setAudioPlayout(true)
						audioRoute.resume()
					}
				}
				is VideoTrack -> {
					dispatch {
						track.setEnabled(true)
						remoteView?.let { view ->
							remoteVideo?.removeSink(view)
							track.addSink(view)
						}
						remoteVideo = track
						media.value = media.value.copy(remoteVideo = true)
					}
				}
			}
		}
	}
}

@Suppress("MagicNumber")
private fun org.webrtc.CameraVideoCapturer.startCallCapture() {
	startCapture(1280, 720, 30)
}
