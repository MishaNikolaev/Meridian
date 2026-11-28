package com.nmichail.meridian.ui.host

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.geometry.Rect

class PassThroughComposeView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

	val compose = ComposeView(context)

	init {
		addView(
			compose,
			LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
		)
	}

	var touchBounds: Rect? = null

	override fun dispatchTouchEvent(event: MotionEvent): Boolean {
		if (event.actionMasked == MotionEvent.ACTION_DOWN) {
			val bounds = touchBounds ?: return false
			if (event.x !in bounds.left..bounds.right || event.y !in bounds.top..bounds.bottom) {
				return false
			}
		}
		return super.dispatchTouchEvent(event)
	}
}
