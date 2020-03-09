/*
 * Copyright 2015-2019 Hive Box.
 */

package com.xzy.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.inputmethodservice.Keyboard.KEYCODE_DELETE
import android.inputmethodservice.Keyboard.KEYCODE_SHIFT
import android.inputmethodservice.KeyboardView
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.ActionMode
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_BACK
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText

class KeyboardEditText : AppCompatEditText, View.OnTouchListener, View.OnFocusChangeListener,
    KeyboardView.OnKeyboardActionListener {
    companion object {
        const val KEYCODE_HIDE: Int = -7
    }

    private var isPassword: Boolean = false
    private var isCapital: Boolean = false
    private lateinit var keyboard: Keyboard
    private lateinit var keyboardView: KeyboardView

    var keyListener: OnKeyboardKeyListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (!isInEditMode && attrs != null) {
            init(context, attrs)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            hideSystemKeyboard(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode) {
            hideSystemKeyboard(this)
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v is EditText) {
            if (hasFocus) {
                isCursorVisible = true
                keyboardView.show()
            } else {
                isCursorVisible = false
                keyboardView.dismiss()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v?.windowToken != null) {
            if (event?.action == ACTION_DOWN) {
                keyboardView.show()
            }
        }
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KEYCODE_BACK) {
            if (keyboardView.isShowing()) {
                keyboardView.dismiss()
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val start = selectionStart
        val editable = text

        when (primaryCode) {
            KEYCODE_DELETE -> {
                if (hasFocus() && !editable.isNullOrEmpty() && start > 0) {
                    editable.delete(start - 1, start)
                }
            }
            KEYCODE_SHIFT -> {
                capsLock()
            }
            KEYCODE_HIDE -> {
                keyboardView.dismiss()
            }
            else -> {
                if (hasFocus()) {
                    editable?.insert(start, primaryCode.toChar().toString())
                }
            }
        }

        keyListener?.onKey(primaryCode)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        var inputType: InputType
        context.obtainStyledAttributes(attrs, R.styleable.KeyboardEditText).apply {
            val inputTypeValue =
                getInt(R.styleable.KeyboardEditText_inputType, InputType.QWERTY.value)
            isPassword = getBoolean(R.styleable.KeyboardEditText_password, false)
            inputType = inputTypeValue.convertToInputType()
            recycle()
        }

        hideSystemKeyboard(this)

        keyboardView = View.inflate(context, R.layout.keyboard, null) as KeyboardView
        keyboard = when (inputType) {
            InputType.NUMBER -> Keyboard(context, R.xml.keyboard_num)
            InputType.QWERTY -> Keyboard(context, R.xml.keyboard_qwerty)
            InputType.NUMBER2 -> Keyboard(context, R.xml.keyboard_num_2)
        }

        keyboardView.isEnabled = true
        keyboardView.isPreviewEnabled = false
        keyboardView.setOnKeyboardActionListener(this)
        keyboardView.keyboard = keyboard

        if (isPassword) {
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        isLongClickable = false
        setOnTouchListener(this)
        setTextIsSelectable(false)
        onFocusChangeListener = this

        customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        }

        isFocusable = true
        isFocusableInTouchMode = true
    }

    @SuppressLint("DefaultLocale")
    private fun capsLock() {
        val keys = keyboard.keys
        keys.forEach {
            when (it.codes[0]) {
                in 65..90 -> {
                    it.label = it.label.toString().toLowerCase()
                    it.codes[0] = it.codes[0] + 32
                }
                in 97..122 -> {
                    it.label = it.label.toString().toUpperCase()
                    it.codes[0] = it.codes[0] - 32
                }
                KEYCODE_SHIFT -> {
                    it.label = null
                    if (isCapital) {
                        it.icon = drawable(R.drawable.keyboard_shift)
                    } else {
                        it.icon = drawable(R.drawable.keyboard_shift_press)
                    }
                }
            }
        }
        keyboardView.invalidateAllKeys()
        isCapital = !isCapital
    }

    fun drawable(drawableResId: Int): Drawable {
        return resources.getDrawable(drawableResId)
    }

    override fun swipeLeft() {
    }

    override fun swipeUp() {
    }

    override fun swipeDown() {
    }

    override fun swipeRight() {
    }

    override fun onText(text: CharSequence?) {
    }

    override fun onPress(primaryCode: Int) {
    }

    override fun onRelease(primaryCode: Int) {
    }
}

internal enum class InputType(var value: Int) {
    NUMBER(0),
    QWERTY(1),
    NUMBER2(2)
}

internal fun Int.convertToInputType(): InputType {
    return when (this) {
        0 -> InputType.NUMBER
        1 -> InputType.QWERTY
        2 -> InputType.NUMBER2
        else -> throw IllegalStateException("Unexpected value: $this.")
    }
}

interface OnKeyboardKeyListener {
    fun onKey(primaryCode: Int)
}