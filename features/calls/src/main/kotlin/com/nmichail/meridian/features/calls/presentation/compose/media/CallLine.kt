package com.nmichail.meridian.features.calls.presentation.compose.media

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack
import org.webrtc.audio.JavaAudioDeviceModule

private const val ICE_LINES = 3

class CallLine(context: Context, private val video: Boolean) {
	private val main = Handler(Looper.getMainLooper())
	private val egl = EglBase.create()
	private val audioManager = context.getSystemService(AudioManager::class.java)
	private val factory: PeerConnectionFactory
	private val audioDevice: JavaAudioDeviceModule
	private val localAudio: AudioTrack
	private val capturer: org.webrtc.CameraVideoCapturer?
	private val localVideo: VideoTrack?
	private var remoteVideo: VideoTrack? = null
	private var remoteView: SurfaceViewRenderer? = null
	private val pending = mutableListOf<IceCandidate>()
	private val queued = ArrayDeque<String>()
	private val seen = HashSet<String>()
	private var remoteReady = false
	private val pc: PeerConnection
	private var restoredMode: Int = AudioManager.MODE_NORMAL
	private val focus: AudioFocusRequest
	@Volatile
	private var sink: ((String) -> Unit)? = null
	var onSignal: ((String) -> Unit)?
		get() = sink
		set(value) {
			sink = value
			if (value == null) {
				return
			}
			val waiting = synchronized(queued) {
				val items = queued.toList()
				queued.clear()
				items
			}
			waiting.forEach(value)
		}
	var remoteOn: Boolean by mutableStateOf(false)
		private set
	var muted: Boolean by mutableStateOf(false)
		private set
	var speaker: Boolean by mutableStateOf(video)
		private set

	private val constraints = MediaConstraints().apply {
		mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
		if (video) {
			mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
		}
	}

	init {
		val previousMode = audioManager.mode
		val attributes = AudioAttributes.Builder()
			.setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
			.setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
			.build()
		focus = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(attributes).build()
		audioManager.requestAudioFocus(focus)
		routeSpeaker(video)
		if (audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL) == 0) {
			audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) / 2, 0)
		}
		PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions())
		audioDevice = JavaAudioDeviceModule.builder(context)
			.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
			.setAudioAttributes(attributes)
			.setUseHardwareAcousticEchoCanceler(true)
			.setUseHardwareNoiseSuppressor(true)
			.createAudioDeviceModule()
		factory = PeerConnectionFactory.builder()
			.setAudioDeviceModule(audioDevice)
			.setVideoEncoderFactory(DefaultVideoEncoderFactory(egl.eglBaseContext, true, true))
			.setVideoDecoderFactory(DefaultVideoDecoderFactory(egl.eglBaseContext))
			.createPeerConnectionFactory()
		localAudio = factory.createAudioTrack("audio", factory.createAudioSource(MediaConstraints()))
		localAudio.setEnabled(true)
		val cameras = Camera2Enumerator(context)
		val cameraName = cameras.deviceNames.firstOrNull { cameras.isFrontFacing(it) } ?: cameras.deviceNames.firstOrNull()
		capturer = if (video) cameraName?.let { cameras.createCapturer(it, null) } else null
		localVideo = capturer?.let { camera ->
			val source = factory.createVideoSource(false)
			camera.initialize(SurfaceTextureHelper.create("meridian-call", egl.eglBaseContext), context, source.capturerObserver)
			camera.startCapture(1280, 720, 30)
			factory.createVideoTrack("local", source)
		}
		val config = PeerConnection.RTCConfiguration(
			listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()),
		)
		config.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
		config.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
		config.enableIceGatheringOnAnyAddressPorts = true
		config.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
		pc = checkNotNull(factory.createPeerConnection(config, peer()))
		pc.setAudioPlayout(true)
		pc.setAudioRecording(true)
		pc.addTrack(localAudio, listOf("call"))
		localVideo?.let { track ->
			track.setEnabled(true)
			pc.addTrack(track, listOf("call"))
		}
		restoredMode = previousMode
	}

	fun setMuted(on: Boolean) {
		if (muted == on) {
			return
		}
		muted = on
		localAudio.setEnabled(!on)
		pc.setAudioRecording(!on)
	}

	fun setSpeaker(on: Boolean) {
		if (speaker == on) {
			return
		}
		speaker = on
		routeSpeaker(on)
	}

	private fun routeSpeaker(on: Boolean) {
		audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			val wanted = if (on) AudioDeviceInfo.TYPE_BUILTIN_SPEAKER else AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
			audioManager.availableCommunicationDevices.firstOrNull { it.type == wanted }?.let(audioManager::setCommunicationDevice)
		}
		audioManager.isSpeakerphoneOn = on
	}

	fun show(renderer: SurfaceViewRenderer, remote: Boolean) {
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
		pc.createOffer(created("offer"), constraints)
	}

	fun take(body: String) {
		if (!seen.add(body) || !body.contains('\n')) {
			return
		}
		val kind = body.substringBefore('\n')
		val rest = body.substringAfter('\n')
		when (kind) {
			"offer" -> pc.setRemoteDescription(
				applied {
					remoteReady = true
					flush()
					pc.createAnswer(created("answer"), constraints)
				},
				SessionDescription(SessionDescription.Type.OFFER, rest),
			)
			"answer" -> pc.setRemoteDescription(
				applied {
					remoteReady = true
					flush()
				},
				SessionDescription(SessionDescription.Type.ANSWER, rest),
			)
			"ice" -> addIce(rest)
		}
	}

	fun stop() {
		onSignal = null
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
		factory.dispose()
		audioDevice.release()
		egl.release()
		audioManager.abandonAudioFocusRequest(focus)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			audioManager.clearCommunicationDevice()
		}
		audioManager.mode = restoredMode
		audioManager.isSpeakerphoneOn = false
	}

	private fun addIce(rest: String) {
		val lines = rest.split('\n', limit = ICE_LINES)
		if (lines.size < ICE_LINES) {
			return
		}
		val index = lines[1].toIntOrNull() ?: return
		val candidate = IceCandidate(lines[0], index, lines[2])
		if (remoteReady) {
			pc.addIceCandidate(candidate)
		} else {
			pending.add(candidate)
		}
	}

	private fun flush() {
		pending.forEach { pc.addIceCandidate(it) }
		pending.clear()
	}

	private fun created(kind: String) = object : SdpObserver {
		override fun onCreateSuccess(description: SessionDescription) {
			pc.setLocalDescription(
				applied {
					val body = "$kind\n${description.description}"
					val listener = sink
					if (listener == null) synchronized(queued) { queued.add(body) } else listener(body)
				},
				description,
			)
		}
		override fun onSetSuccess() = Unit
		override fun onCreateFailure(error: String) = Unit
		override fun onSetFailure(error: String) = Unit
	}

	private fun applied(onOk: () -> Unit) = object : SdpObserver {
		override fun onCreateSuccess(description: SessionDescription) = Unit
		override fun onSetSuccess() {
			onOk()
		}
		override fun onCreateFailure(error: String) = Unit
		override fun onSetFailure(error: String) = Unit
	}

	private fun peer() = object : PeerConnection.Observer {
		fun hand(body: String) {
			val listener = sink
			if (listener == null) synchronized(queued) { queued.add(body) } else listener(body)
		}
		override fun onSignalingChange(state: PeerConnection.SignalingState) = Unit
		override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
			val connected = state == PeerConnection.IceConnectionState.CONNECTED || state == PeerConnection.IceConnectionState.COMPLETED
			if (!connected) {
				return
			}
			main.post {
				routeSpeaker(speaker)
				pc.setAudioPlayout(true)
				pc.setAudioRecording(!muted)
			}
		}
		override fun onIceConnectionReceivingChange(receiving: Boolean) = Unit
		override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) = Unit
		override fun onIceCandidate(candidate: IceCandidate) {
			hand("ice\n${candidate.sdpMid.orEmpty()}\n${candidate.sdpMLineIndex}\n${candidate.sdp}")
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
					track.setEnabled(true)
					main.post {
						pc.setAudioPlayout(true)
						audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
					}
				}
				is VideoTrack -> {
					track.setEnabled(true)
					remoteVideo = track
					remoteView?.let { view -> track.addSink(view) }
					main.post { remoteOn = true }
				}
			}
		}
	}
}
