package com.nmichail.meridian.features.calls.catalog.data.media

import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

internal class WebRtcNegotiation(
	private val pc: PeerConnection,
	private val constraints: MediaConstraints,
	private val dispatch: (() -> Unit) -> Unit,
	private val emitSignal: (String) -> Unit,
) {
	private val pending = mutableListOf<IceCandidate>()
	private val receivedSignals = HashSet<String>()
	private var remoteReady = false

	fun offer() {
		pc.createOffer(created("offer"), constraints)
	}

	fun take(body: String) {
		if (!receivedSignals.add(body) || !body.contains('\n')) {
			return
		}
		val kind = body.substringBefore('\n')
		val rest = body.substringAfter('\n')
		when (kind) {
			"offer" -> pc.setRemoteDescription(
				applied {
					remoteReady = true
					applyPendingIceCandidates()
					pc.createAnswer(created("answer"), constraints)
				},
				SessionDescription(SessionDescription.Type.OFFER, rest),
			)
			"answer" -> pc.setRemoteDescription(
				applied {
					remoteReady = true
					applyPendingIceCandidates()
				},
				SessionDescription(SessionDescription.Type.ANSWER, rest),
			)
			"ice" -> addIce(rest)
		}
	}

	@Suppress("MagicNumber")
	private fun addIce(rest: String) {
		val lines = rest.split('\n', limit = 3)
		if (lines.size < 3) {
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

	private fun applyPendingIceCandidates() {
		pending.forEach { candidate -> pc.addIceCandidate(candidate) }
		pending.clear()
	}

	private fun created(kind: String) = object : SdpObserver {
		override fun onCreateSuccess(description: SessionDescription) {
			dispatch {
				pc.setLocalDescription(
					applied {
						val body = "$kind\n${description.description}"
						emitSignal(body)
					},
					description,
				)
			}
		}
		override fun onSetSuccess() = Unit
		override fun onCreateFailure(error: String) = Unit
		override fun onSetFailure(error: String) = Unit
	}

	private fun applied(onOk: () -> Unit) = object : SdpObserver {
		override fun onCreateSuccess(description: SessionDescription) = Unit
		override fun onSetSuccess() {
			dispatch(onOk)
		}
		override fun onCreateFailure(error: String) = Unit
		override fun onSetFailure(error: String) = Unit
	}
}