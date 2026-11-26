package com.nmichail.meridian.features.calls.catalog.presentation.compose.media

import android.content.Context
import org.webrtc.Camera2Enumerator
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

internal class CallCapture(context: Context) {
	private val egl = EglBase.create()
	private val factory: PeerConnectionFactory
	private val capturer = frontCamera(context)
	val track: VideoTrack
	var cameraOn: Boolean = true
		private set

	init {
		PeerConnectionFactory.initialize(
			PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions(),
		)
		factory = PeerConnectionFactory.builder()
			.setVideoEncoderFactory(DefaultVideoEncoderFactory(egl.eglBaseContext, true, true))
			.setVideoDecoderFactory(DefaultVideoDecoderFactory(egl.eglBaseContext))
			.createPeerConnectionFactory()
		val source = factory.createVideoSource(false)
		capturer.initialize(
			SurfaceTextureHelper.create("meridian-call", egl.eglBaseContext),
			context,
			source.capturerObserver,
		)
		capturer.openPreview(width = 1280, height = 720, frames = 30)
		track = factory.createVideoTrack("local", source)
	}

	fun bind(renderer: SurfaceViewRenderer) {
		renderer.init(egl.eglBaseContext, null)
		renderer.setMirror(true)
		renderer.setEnableHardwareScaler(true)
		track.addSink(renderer)
	}

	fun setCamera(on: Boolean) {
		if (cameraOn == on) {
			return
		}
		if (on) {
			capturer.openPreview(width = 1280, height = 720, frames = 30)
		} else {
			capturer.stopCapture()
		}
		cameraOn = on
		track.setEnabled(on)
	}

	fun stop() {
		if (cameraOn) {
			capturer.stopCapture()
			cameraOn = false
		}
		capturer.dispose()
		track.dispose()
		factory.dispose()
		egl.release()
	}

	private fun frontCamera(context: Context): org.webrtc.CameraVideoCapturer {
		val cameras = Camera2Enumerator(context)
		val name = cameras.deviceNames.firstOrNull { cameras.isFrontFacing(it) } ?: cameras.deviceNames.first()
		return cameras.createCapturer(name, null)
	}
}

internal fun org.webrtc.CameraVideoCapturer.openPreview(width: Int, height: Int, frames: Int) {
	startCapture(width, height, frames)
}