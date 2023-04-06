package com.scouto.assignment.utils

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

fun getRandomString(sizeOfRandomString: Int): String? {
    val ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm"
    val random = Random()
    val sb = StringBuilder(sizeOfRandomString)
    for (i in 0 until sizeOfRandomString) sb.append(
        ALLOWED_CHARACTERS[random.nextInt(
            ALLOWED_CHARACTERS.length
        )]
    )
    return sb.toString()
}


fun setTextChangeListenerOnThisEditTextAndRemoveErrorWhenTextChanged(
    textInputLayout: TextInputLayout?,
    et: TextInputEditText?
) {
    try {
        et?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    textInputLayout?.error = null
                } catch (e: Exception) {
                    Log.d("error",e.message.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    } catch (e: Exception) {
        Log.d("error",e.message.toString())
    }
}

fun isNullOrEmptyOrTrimmedLengthIsZero(str: String?): Boolean {
    return if (str != null && !str.isEmpty() && str.trim { it <= ' ' }.length != 0) false else true
}

fun showAutomaticSoftFocusOrOpenKeyboardOnThisEditext(context: Context?, view: View) {
    context?.let {
        view.post {
            view.requestFocus()
            val lManager = it
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            lManager.toggleSoftInput(
                InputMethodManager.SHOW_IMPLICIT,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
            lManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}

fun isInvalidEmail(str:String?):Boolean{
    if (!TextUtils.isEmpty(str)) {
        return !Patterns.EMAIL_ADDRESS.matcher(str!!).matches()
    }
    return true
}

