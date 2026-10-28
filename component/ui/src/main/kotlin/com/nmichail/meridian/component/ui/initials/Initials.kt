package com.nmichail.meridian.component.ui.initials

import com.nmichail.meridian.core.validation.initials.InitialsValidator

fun initials(name: String): String = InitialsValidator.of(name)
