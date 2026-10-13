package com.nmichail.meridian.di

import com.nmichail.meridian.features.home.di.HomeComponentHolder

fun AppComponent.installComponents() {
	HomeComponentHolder.init(this)
}
