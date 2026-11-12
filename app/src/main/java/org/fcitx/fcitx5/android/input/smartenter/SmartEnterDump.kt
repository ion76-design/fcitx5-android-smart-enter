/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.smartenter

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SmartEnterDump {
    /**
     * Save a diagnostic dump. Android 10+: public Download folder via MediaStore (no permission needed);
     * older: app's external files dir.
     * @return a human readable location, or null on failure
     */
    fun save(context: Context, packageName: String, content: String): String? = runCatching {
        val time = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.ROOT).format(Date())
        val name = "fcitx5-smart-enter-dump-$packageName-$time.txt"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return@runCatching null
            resolver.openOutputStream(uri)?.use { it.write(content.toByteArray()) }
                ?: return@runCatching null
            "Download/$name"
        } else {
            val dir = context.getExternalFilesDir(null) ?: return@runCatching null
            val file = File(dir, name)
            file.writeText(content)
            file.absolutePath
        }
    }.getOrNull()
}
