package com.xzy.keyboard

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.PixelFormat
import android.inputmethodservice.KeyboardView
import android.os.Build
import android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.os.Build.VERSION_CODES.KITKAT
import android.text.InputType.TYPE_NULL
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent.ACTION_UP
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.lang.ref.WeakReference
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

val osVerCode: Int get() = Build.VERSION.SDK_INT
val keyboardViews: ConcurrentHashMap<Int, WeakReference<KeyboardView>> = ConcurrentHashMap()
fun hideSystemKeyboard(editText: EditText) {
    val methodName: String? = when {
        osVerCode >= JELLY_BEAN -> "setShowSoftInputOnFocus" // 4.2
        osVerCode >= ICE_CREAM_SANDWICH -> "setSoftInputShownOnFocus" // 4.0
        else -> null
    }

    if (methodName == null) {
        editText.inputType = TYPE_NULL
    } else {
        val cls = EditText::class.java
        val method: Method
        try {
            method = cls.getMethod(methodName, Boolean::class.java)
            method.isAccessible = true
            method.invoke(editText, false)
        } catch (e: Exception) {
            editText.inputType = TYPE_NULL
        }
    }
}

fun KeyboardView.isShowing() = parent != null

fun KeyboardView.show() {
    if (isShowing()) {
        return
    }
    val layoutParams = WindowManager.LayoutParams()
    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
    layoutParams.flags =
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    layoutParams.format = PixelFormat.TRANSLUCENT
    layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

    val metrics = DisplayMetrics()
    val realMetrics = DisplayMetrics()
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(metrics)
    windowManager.defaultDisplay.getRealMetrics(realMetrics)

    if (osVerCode > KITKAT && realMetrics.heightPixels > metrics.heightPixels) {
        var navigationBarHeight = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        layoutParams.y = navigationBarHeight
    }

    layoutParams.windowAnimations = R.style.KeyboardAnim
    windowManager.addView(this, layoutParams)
    keyboardViews[hashCode()] = WeakReference(this)
}

fun KeyboardView.dismiss() {
    if (isShowing()) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeViewImmediate(this)
    }
    keyboardViews.remove(hashCode())
}

fun dismissAllKeyboards() {
    keyboardViews.forEach { (_, value) ->
        val keyboardView = value.get()
        keyboardView?.dismiss()
    }
}

@Suppress("unused")
fun Activity.collapseOnBlankArea() {
    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    window.decorView.setOnTouchListener { v, event ->
        when (event?.action) {
            ACTION_UP -> v?.performClick()
        }
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        } else {
            inputMethodManager.hideSoftInputFromWindow(
                findViewById<View>(android.R.id.content).windowToken,
                0
            )
        }
        dismissAllKeyboards()
        false
    }
}