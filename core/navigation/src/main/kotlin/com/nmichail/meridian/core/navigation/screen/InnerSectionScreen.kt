package com.nmichail.meridian.core.navigation.screen

import androidx.fragment.app.Fragment

class InnerSectionScreen(
	key: String,
	fragmentClass: Class<out Fragment>,
) : ScreenWrapper(key, fragmentClass)
