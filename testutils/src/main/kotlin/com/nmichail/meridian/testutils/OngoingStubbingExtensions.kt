package com.nmichail.meridian.testutils

import kotlinx.coroutines.awaitCancellation
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.stubbing.OngoingStubbing

fun <T> OngoingStubbing<T>.thenNeverAnswer() {
	doSuspendableAnswer { awaitCancellation() }
}

infix fun <T> OngoingStubbing<T>.doThrowSafe(exception: Exception): OngoingStubbing<T> =
	thenAnswer { throw exception }
