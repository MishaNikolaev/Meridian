package com.nmichail.meridian.features.profile.about.presentation

import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(TestCoroutineExtension::class)
class AboutViewModelTest {
	private val router: AboutRouter = mock()
	private val viewModel = AboutViewModel(router)

	@Test
	fun `back EXPECT previous screen`() = runTest {
		viewModel.applyIntent(AboutIntent.Back)

		verify(router).navigateBack()
	}

	@Test
	fun `repository EXPECT link`() = runTest {
		val url = "https://github.com/MishaNikolaev/Meridian"

		viewModel.applyIntent(AboutIntent.OpenRepository(url))

		verify(router).openLink(url)
	}
}