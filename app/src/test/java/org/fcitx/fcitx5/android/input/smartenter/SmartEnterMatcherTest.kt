/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.smartenter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartEnterMatcherTest {

    @Test
    fun twitterComposerIdsAreSendButtons() {
        listOf(
            "tweet_button",
            "button_tweet",
            "composer_post",
            "dm_composer_send_button",
            "dm_send_button",
            "send_button",
            "composer_send_button",
        ).forEach { id ->
            val full = "com.twitter.android:id/$id"
            assertTrue(id, SmartEnterMatcher.isKnownSendId(SmartEnterMatcher.TWITTER, full))
            assertFalse(id, SmartEnterMatcher.isBlocked(SmartEnterMatcher.TWITTER, full))
        }
    }

    @Test
    fun twitterOverflowAndEngagementIdsAreBlocked() {
        listOf(
            "tweet_curation_action",
            "inline_reply",
            "inline_retweet",
            "inline_like",
            "inline_bookmark",
            "inline_twitter_share",
            "inline_view_count",
            "tweet_grok_analyse_post",
            "tweet_reply_context",
            "persistent_reply",
        ).forEach { id ->
            assertTrue(id, SmartEnterMatcher.isBlocked(SmartEnterMatcher.TWITTER, "com.twitter.android:id/$id"))
            assertFalse(id, SmartEnterMatcher.isKnownSendId(SmartEnterMatcher.TWITTER, id))
        }
    }

    @Test
    fun curationTokenIsBlockedInAnyPackage() {
        assertTrue(SmartEnterMatcher.isBlocked("com.example.chat", "com.example.chat:id/tweet_curation_action"))
        assertFalse(SmartEnterMatcher.isBlocked("com.instagram.android", "com.instagram.android:id/inline_reply"))
        assertFalse(SmartEnterMatcher.isKnownSendId("com.instagram.android", "com.instagram.android:id/tweet_button"))
    }

    @Test
    fun replyAndPostLabelsAreExact() {
        listOf(
            "reply", "post", "tweet", "回复", "发布", "發布", "回覆", "post comment",
            "reply button", "回复按钮",
        ).forEach {
            assertTrue(it, SmartEnterMatcher.isExactPostLabel(it))
        }
        assertTrue(SmartEnterMatcher.TWITTER_LABELS.contains("回复"))
        assertTrue(SmartEnterMatcher.TWITTER_LABELS.contains("reply"))
        assertTrue(SmartEnterMatcher.TWITTER_LABELS.contains("post"))
    }

    @Test
    fun sentencesThatMentionPostAreNotSendLabels() {
        listOf(
            "more options for this post",
            "share post",
            "save a post to read later",
            "32 replies. reply",
            "2条回复",
            "32条回复",
            "更多",
            "更多选项",
            "post your reply",
            "发布你的回复",
        ).forEach {
            assertFalse(it, SmartEnterMatcher.isExactPostLabel(SmartEnterMatcher.norm(it)))
        }
    }

    @Test
    fun sendPhrasesStillMatch() {
        assertTrue(SmartEnterMatcher.matchesSendPhrase("send"))
        assertTrue(SmartEnterMatcher.matchesSendPhrase("tap to send"))
        assertTrue(SmartEnterMatcher.matchesSendPhrase("send message"))
        assertTrue(SmartEnterMatcher.matchesSendPhrase("发送"))
        assertTrue(SmartEnterMatcher.matchesSendPhrase("发送消息"))
        assertFalse(SmartEnterMatcher.matchesSendPhrase("more options for this post"))
        assertFalse(SmartEnterMatcher.matchesSendPhrase("回复"))
    }

    @Test
    fun idTokensSplitCamelCaseAndSymbols() {
        assertEquals(setOf("tweet", "button"), SmartEnterMatcher.idTokens("tweet_button"))
        assertEquals(setOf("tweet", "button"), SmartEnterMatcher.idTokens("tweetButton"))
        assertEquals(
            setOf("tweet", "curation", "action"),
            SmartEnterMatcher.idTokens("tweet_curation_action")
        )
    }
}
