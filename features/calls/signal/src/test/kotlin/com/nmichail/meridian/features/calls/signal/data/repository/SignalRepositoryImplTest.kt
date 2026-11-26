package com.nmichail.meridian.features.calls.signal.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.calls.signal.data.api.SignalApi
import com.nmichail.meridian.features.calls.signal.domain.entity.SignalCall
import com.nmichail.meridian.features.calls.signal.data.dto.SignalMessageDto
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.cipher.MessageCipher
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.dto.ChatMemberDto
import com.nmichail.meridian.shared.chat.data.dto.DeviceBundleDto
import com.nmichail.meridian.shared.chat.data.dto.KeyDto
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SignalRepositoryImplTest {

	private val api: SignalApi = mock()
	private val chats: ChatApi = mock()
	private val members: MemberDataSource = mock()
	private val sessionRepository: SessionRepository = mock()
	private val statement: HttpStatement = mock()
	private val repository = SignalRepositoryImpl(
		api,
		chats,
		members,
		sessionRepository,
	)
	private val call = SignalCall(callId = "call-1", chatId = "chat-1")
	private val self: ChatMemberDto = mock { on { userId } doReturn "self" }
	private val another: ChatMemberDto = mock { on { userId } doReturn "id_another" }
	private val chat: ChatDetailsDto = mock {
		on { members } doReturn listOf(self, another)
	}

	@Test
	fun `send signal EXPECT id_another devices`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(chats.getChat(call.chatId)) doReturn chat
		whenever(members.getKeys("id_another")) doReturn KeyDto(
			userId = "id_another",
			devices = listOf(
				DeviceBundleDto("device-a"),
				DeviceBundleDto("device-a"),
				DeviceBundleDto("device-b"),
			),
		)
		whenever(api.send(any(), any())) doReturn statement

		repository.sendSignal(call, "offer")

		verify(api).send(
			eq(call.callId),
			check { request ->
				assertEquals(
					listOf("device-a", "device-b"),
					request.devices.map { device -> device.deviceId },
				)
			},
		)
	}

	@Test
	fun `send again EXPECT devices read once`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(chats.getChat(call.chatId)) doReturn chat
		whenever(members.getKeys("id_another")) doReturn KeyDto(
			userId = "id_another",
			devices = listOf(DeviceBundleDto("device-a")),
		)
		whenever(api.send(any(), any())) doReturn statement

		repository.sendSignal(call, "offer")
		repository.sendSignal(call, "ice")

		verify(members, times(1)).getKeys("id_another")
	}

	@Test
	fun `missing session EXPECT illegal state`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn null

		assertThrows<IllegalStateException> {
			repository.sendSignal(call, "offer")
		}
	}

	@Test
	fun `no devices EXPECT illegal state`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(chats.getChat(call.chatId)) doReturn chat
		whenever(members.getKeys("id_another")) doReturn KeyDto(userId = "id_another", devices = emptyList())

		assertThrows<IllegalStateException> {
			repository.sendSignal(call, "offer")
		}
	}

	@Test
	fun `load signals EXPECT plain text`() = runTest {
		val cipher = MessageCipher.encrypt(call.chatId, "answer")
		whenever(api.load(call.callId)) doReturn listOf(
			SignalMessageDto(senderDeviceId = "device-a", body = cipher),
			SignalMessageDto(senderDeviceId = "device-b", body = "###"),
		)

		val actual = repository.loadSignals(call)

		assertEquals(listOf("answer"), actual)
	}
}