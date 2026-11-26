/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2026 Fcitx5 for Android Contributors
 */

import org.gradle.api.JavaVersion

object Versions {

    val java = JavaVersion.VERSION_11
    const val compileSdk = 36
    const val minSdk = 23
    const val targetSdk = 36

    const val defaultCMake = "3.31.6"
    const val defaultNDK = "28.0.13004108"
    const val defaultBuildTools = "36.1.0"

    // NOTE: increase this value to bump version code.
    // versionCode is baseVersionCode * 10 + abi id (arm64-v8a is 2), so 13 ships as 132
    // and installs over smart-enter-v2's arm64 build (122).
    const val baseVersionCode = 13
    // CMake project() VERSION. Must stay numeric (major.minor.patch).
    const val baseVersionName = "0.1.3"
    // Android versionName for this fork. Not derived from git describe, which still
    // reports the smart-enter-v2 tag until a new tag exists.
    const val appVersionName = "smart-enter-v2.1"

    val supportedABIs = setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
    const val fallbackABI = "arm64-v8a"

    fun calculateVersionCode(abi: String = fallbackABI): Int {
        val abiId = when (abi) {
            "armeabi-v7a" -> 1
            "arm64-v8a" -> 2
            "x86" -> 3
            "x86_64" -> 4
            else -> 0
        }
        return baseVersionCode * 10 + abiId
    }
}
