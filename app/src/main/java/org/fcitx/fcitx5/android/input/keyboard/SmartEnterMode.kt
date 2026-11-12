/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.keyboard

import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.data.prefs.ManagedPreferenceEnum

enum class SmartEnterMode(override val stringRes: Int) : ManagedPreferenceEnum {
    Off(R.string.smart_enter_off),
    ListedApps(R.string.smart_enter_listed_apps),
    AllApps(R.string.smart_enter_all_apps);

    companion object {
        const val DEFAULT_PACKAGES =
            "com.twitter.android, com.instagram.android, jp.naver.line.android, com.kakao.talk"
    }
}

enum class SmartEnterFallback(override val stringRes: Int) : ManagedPreferenceEnum {
    // plain Enter key event: newline in most apps, or "Enter to send" if the app has that setting
    EnterKey(R.string.smart_enter_fallback_enter),
    // IME_ACTION_SEND: some apps send, some (eg. X) just hide the keyboard
    EditorAction(R.string.smart_enter_fallback_action);
}
