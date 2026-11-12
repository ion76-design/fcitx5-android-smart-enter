/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.smartenter

import android.text.InputType
import android.view.inputmethod.EditorInfo
import org.fcitx.fcitx5.android.data.prefs.AppPrefs
import org.fcitx.fcitx5.android.input.keyboard.SmartEnterMode
import splitties.bitflags.hasFlag

/**
 * "Smart Enter": in chat apps whose message box doesn't declare an editor action
 * (multi-line text field, IME_FLAG_NO_ENTER_ACTION, action none/unspecified),
 * show a "send" return key and try to send the message instead of inserting a newline.
 */
object SmartEnter {

    private val prefs by lazy { AppPrefs.getInstance().keyboard }

    private var cachedRaw: String? = null
    private var cachedSet: Set<String> = emptySet()

    private fun packageSet(): Set<String> {
        val raw = prefs.smartEnterPackages.getValue()
        if (raw != cachedRaw) {
            cachedRaw = raw
            cachedSet = raw.split(',', ';', ' ', '\n', '\t', '，')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
        }
        return cachedSet
    }

    /**
     * whether Smart Enter should take over the return key for this editor
     */
    fun isActive(info: EditorInfo?): Boolean {
        info ?: return false
        val mode = prefs.smartEnterMode.getValue()
        if (mode == SmartEnterMode.Off) return false
        val pkg = info.packageName ?: return false
        if (mode == SmartEnterMode.ListedApps && pkg !in packageSet()) return false
        // only plain text fields; leave numbers, phones, dates and passwords alone
        val type = info.inputType
        if (type and InputType.TYPE_MASK_CLASS != InputType.TYPE_CLASS_TEXT) return false
        when (type and InputType.TYPE_MASK_VARIATION) {
            InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_URI,
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS -> return false
        }
        if (info.imeOptions.hasFlag(EditorInfo.IME_FLAG_NO_ENTER_ACTION)) return true
        return when (info.imeOptions and EditorInfo.IME_MASK_ACTION) {
            EditorInfo.IME_ACTION_UNSPECIFIED, EditorInfo.IME_ACTION_NONE -> true
            else -> false
        }
    }
}
