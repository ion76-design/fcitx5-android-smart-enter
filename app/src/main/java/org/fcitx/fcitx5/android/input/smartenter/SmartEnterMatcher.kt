/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.smartenter

import java.util.Locale

/**
 * Pure label / view-id policy for [SmartEnterAccessibilityService].
 *
 * The official X app's reply composer does not use the DM send button. The control that posts a
 * reply is [TWITTER_SEND_IDS] (`tweet_button` and friends), labeled Reply / Post / 回复 / 发布, and
 * it sits in the toolbar above the text field. The comment being replied to stays on screen, and
 * its three-dot overflow (`tweet_curation_action`) is the button that opens the follow / hide /
 * block / report sheet. Matching any nearby text that merely contains "post", "reply", or "回复"
 * clicks that overflow instead of sending.
 */
internal object SmartEnterMatcher {

    const val TWITTER = "com.twitter.android"

    val TWITTER_SEND_IDS = listOf(
        // reply / post composer (toolbar button above the field)
        "tweet_button",
        "button_tweet",
        "composer_post",
        // direct messages, beside the field
        "dm_composer_send_button",
        "dm_send_button",
        "send_button",
        "composer_send_button",
    )

    /**
     * Exact button labels for the X composer and DM box. Compared after [norm], so Latin entries
     * are lowercase. The whole label must equal one of these; a sentence that merely contains the
     * word is not a send button.
     */
    val TWITTER_LABELS = listOf(
        "send", "send message",
        "reply", "post", "tweet",
        "发送", "傳送", "送信", "보내기", "전송",
        "回复", "回覆", "发布", "發佈", "發布", "发帖", "發帖", "发布回复", "發布回覆",
        "回复按钮", "发布按钮", "reply button", "post button",
        "返信", "ポスト", "投稿", "게시", "답글",
        "responder", "répondre", "antworten", "publicar",
    )

    /**
     * Opens the post overflow sheet, or is an engagement control (reply count, like, share).
     * `inline_reply` only starts a reply; it does not send one.
     */
    private val TWITTER_BLOCK_IDS = setOf(
        "tweet_curation_action",
        "inline_reply",
        "inline_retweet",
        "inline_like",
        "inline_bookmark",
        "inline_twitter_share",
        "inline_view_count",
        "inline_action_item_icon",
        "tweet_grok_analyse_post",
        "tweet_reply_context",
        "tweet_profile_image",
        "tweet_profile_image_container",
        "persistent_reply",
        "tweet_box",
    )

    private val BLOCK_ID_TOKENS = setOf(
        "overflow", "curation", "caret", "bookmark", "retweet", "grok"
    )

    // strong "send" wording. whole-word for Latin, substring for CJK ("发送消息")
    private val SEND_WORDS = listOf(
        "send", "send message", "send prompt", "submit", "发送", "發送", "傳送", "送出", "送信", "送る",
        "전송", "보내기", "enviar", "envoyer", "senden", "invia", "отправить", "kirim", "gửi", "ส่ง", "gönder"
    )

    // the entire label must equal one of these. "more options for this post" and "2条回复" do not.
    private val POST_LABELS = listOf(
        "post", "reply", "tweet", "comment", "post comment",
        "发布", "發佈", "發布", "发帖", "發帖", "回复", "回覆", "发布回复", "發布回覆",
        "回复按钮", "发布按钮", "reply button", "post button",
        "评论", "留言", "投稿", "返信", "ポスト", "게시", "답글", "댓글", "publicar", "responder"
    )

    fun norm(s: CharSequence?): String =
        s?.toString()?.trim()?.lowercase(Locale.ROOT) ?: ""

    fun idEntry(viewIdResourceName: String?): String =
        viewIdResourceName?.substringAfter(":id/") ?: ""

    fun idTokens(rawId: String): Set<String> =
        rawId.replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
            .lowercase(Locale.ROOT)
            .split(Regex("[^a-z0-9]+"))
            .filter { it.isNotEmpty() }
            .toSet()

    fun isKnownSendId(packageName: String, viewIdResourceName: String?): Boolean {
        if (packageName != TWITTER) return false
        val entry = idEntry(viewIdResourceName)
        return entry.isNotEmpty() && entry in TWITTER_SEND_IDS
    }

    fun isBlocked(packageName: String, viewIdResourceName: String?): Boolean {
        val entry = idEntry(viewIdResourceName)
        if (entry.isEmpty()) return false
        if (idTokens(entry).any { it in BLOCK_ID_TOKENS }) return true
        return packageName == TWITTER && entry in TWITTER_BLOCK_IDS
    }

    /** the whole label is a Post/Reply style button, not a sentence that mentions one */
    fun isExactPostLabel(normalized: String): Boolean =
        normalized.isNotEmpty() && normalized.length <= 24 && normalized in POST_LABELS

    /**
     * "send", "send message", "tap to send", "发送消息".
     * Send wording is specific enough for a whole-word / CJK-substring match.
     */
    fun matchesSendPhrase(normalized: String): Boolean {
        if (normalized.isEmpty() || normalized.length > 40) return false
        return SEND_WORDS.any { w ->
            if (normalized == w) return@any true
            if (w.all { it.code < 128 }) {
                Regex("(^|[^\\p{L}])${Regex.escape(w)}([^\\p{L}]|$)").containsMatchIn(normalized)
            } else {
                normalized.contains(w)
            }
        }
    }
}
