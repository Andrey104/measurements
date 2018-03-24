package ru.nextf.measurements

import android.text.TextUtils
import java.nio.file.Files.delete
import android.text.Editable
import android.text.TextWatcher


/**
 * Created by left0ver on 24.03.18.
 */
class OwnWatcher : TextWatcher {

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable) {
        // Remove spacing char
//        if (s.length > 0 && s.length % 4 == 0) {
//            val c = s[s.length - 1]
//            if (space == c) {
//                s.delete(s.length - 3, s.length)
//            }
//        }
        // Insert char where needed.
        if (s.isNotEmpty() && s.length == 4) {
            val c = s[s.length - 3]
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), space.toString()).size <= 3) {
                s.insert(s.length - 3, space.toString())
            }
        }
        if (s.isNotEmpty() && s.length == 6) {
            val c = s[s.length - 5]
            if (space == c) {
                s.delete(s.length - 5, s.length - 4)
            }
            val ca = s[s.length - 3]
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(ca) && TextUtils.split(s.toString(), space.toString()).size <= 3) {
                s.insert(s.length - 3, space.toString())
            }
        }
        if (s.isNotEmpty() && s.length == 8) {
            val c = s[s.length - 6]
            if (space == c) {
                s.delete(s.length - 6, s.length - 5)
            }
            val ca = s[s.length - 3]
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(ca) && TextUtils.split(s.toString(), space.toString()).size <= 3) {
                s.insert(s.length - 3, space.toString())
            }
        }
    }

    companion object {

        // Change this to what you want... ' ', '-' etc..
        private val space = ' '
    }
}