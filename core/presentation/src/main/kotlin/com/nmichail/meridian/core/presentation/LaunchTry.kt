package com.nmichail.meridian.core.presentation

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun CoroutineScope.launchTry(tryBlock: suspend CoroutineScope.() -> Unit): LaunchBuilder =
	LaunchBuilder(
		tryBlock = tryBlock,
		scope = this,
	)

class LaunchBuilder(
	private val tryBlock: suspend CoroutineScope.() -> Unit,
	private val scope: CoroutineScope,
) {

	infix fun catch(catchBlock: (Exception) -> Unit): Job {
		val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
			val exception = throwable as? Exception ?: throw throwable
			catchBlock(exception)
		}
		return scope.launch(exceptionHandler) {
			tryBlock()
		}
	}

	fun ignore(): Job = catch { ignored ->
		ignored.ignore()
	}
}

private fun Exception.ignore() = Unit
