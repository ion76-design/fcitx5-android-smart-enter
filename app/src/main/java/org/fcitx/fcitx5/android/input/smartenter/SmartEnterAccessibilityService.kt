/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.smartenter

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import java.lang.ref.WeakReference
import java.util.Locale
import kotlin.math.abs

/**
 * Optional helper for Smart Enter.
 *
 * It does nothing on its own: all accessibility events are ignored (they are only subscribed so that
 * the system node cache stays up to date), it keeps no state and logs nothing.
 * Only when the user presses the Smart Enter key in fcitx5, the IME (same process) asks it to look
 * at the app window once and click the button that looks like "Send" next to the focused input.
 */
class SmartEnterAccessibilityService : AccessibilityService() {

    /**
     * Known send buttons of some apps. Resource ids are matched by their entry name (after ":id/"),
     * descriptions are matched against contentDescription / short text, case-insensitively.
     * These come from public automation scripts and may change with app updates;
     * the generic heuristic runs afterwards anyway.
     */
    private class AppRule(
        val ids: List<String> = emptyList(),
        val labels: List<String> = emptyList(),
        // click the rightmost unlabeled image button in the input row (eg. TikTok's send arrow)
        val unlabeledRightmost: Boolean = false
    )

    companion object {
        @Volatile
        private var instanceRef: WeakReference<SmartEnterAccessibilityService>? = null

        val instance: SmartEnterAccessibilityService?
            get() = instanceRef?.get()

        private const val MAX_NODES = 3000

        private val TIKTOK_RULE = AppRule(
            labels = listOf("send", "post", "post comment", "发送", "发布", "傳送", "發佈", "送信", "投稿", "전송", "게시"),
            unlabeledRightmost = true
        )

        private val RULES = mapOf(
            "com.kakao.talk" to AppRule(
                ids = listOf("send", "send_button"),
                labels = listOf("전송", "send", "发送", "傳送", "送信")
            ),
            "com.twitter.android" to AppRule(
                ids = listOf("dm_composer_send_button", "dm_send_button", "send_button", "composer_send_button"),
                labels = listOf("send", "send message", "发送", "傳送", "送信", "보내기", "전송")
            ),
            "jp.naver.line.android" to AppRule(
                ids = listOf("chathistory_send_button_image", "chathistory_send_button"),
                labels = listOf("send", "傳送", "发送", "送信", "전송")
            ),
            "com.instagram.android" to AppRule(
                ids = listOf("row_thread_composer_send_button_container", "row_thread_composer_button_send", "layout_comment_thread_post_button_click_area"),
                labels = listOf("send", "发送", "傳送", "送信", "보내기", "post", "发布")
            ),
            "com.google.android.apps.bard" to AppRule(
                labels = listOf("send message", "send prompt", "send", "submit", "发送消息", "发送提示", "发送", "傳送訊息", "傳送", "メッセージを送信", "送信", "메시지 보내기", "보내기")
            ),
            "com.google.android.googlequicksearchbox" to AppRule(
                labels = listOf("send message", "send prompt", "send", "submit", "发送消息", "发送提示", "发送", "傳送")
            ),
            "com.openai.chatgpt" to AppRule(
                labels = listOf("send prompt", "send message", "send", "发送提示", "发送消息", "发送", "傳送")
            ),
            "com.zhiliaoapp.musically" to TIKTOK_RULE,
            "com.ss.android.ugc.trill" to TIKTOK_RULE,
            "com.ss.android.ugc.aweme" to TIKTOK_RULE,
        )

        // words that strongly indicate a "send" button, compared against normalized text / description
        private val SEND_WORDS = listOf(
            "send", "send message", "send prompt", "submit", "发送", "發送", "傳送", "送出", "送信", "送る",
            "전송", "보내기", "enviar", "envoyer", "senden", "invia", "отправить", "kirim", "gửi", "ส่ง", "gönder"
        )

        // weaker matches: social apps' "Post"/"Reply" buttons
        private val POST_WORDS = listOf(
            "post", "reply", "tweet", "comment", "发布", "發佈", "發布", "发帖", "回复", "回覆",
            "评论", "留言", "投稿", "返信", "ポスト", "게시", "답글", "댓글", "publicar", "responder"
        )

        // view id tokens, eg. "send_button", "btnSend", "chat_send"; but not "sender_name"
        private val SEND_ID_TOKENS = setOf("send", "submit")
        private val POST_ID_TOKENS = setOf("post", "reply", "tweet", "comment")
        private val BUTTON_ID_TOKENS = setOf("button", "btn")

        // never click these even if they also match
        private val NEGATIVE_PARTS = listOf(
            "voice", "audio", "record", "mic", "sticker", "emoji", "gif", "camera", "photo",
            "gallery", "attach", "heart", "語音", "语音", "录音", "貼圖", "贴图",
            "表情", "相机", "相機", "照片", "图片", "圖片", "음성", "스티커", "이모티콘", "사진", "スタンプ",
            "音声", "写真", "schedule", "定时", "later", "@", "mention", "提及"
        )
        private val NEGATIVE_ID_TOKENS = setOf(
            "voice", "audio", "record", "mic", "sticker", "emoji", "emoticon", "gif", "camera",
            "gallery", "attach", "media", "plus", "more", "at", "mention", "like", "keyboard"
        )

        private fun norm(s: CharSequence?): String =
            s?.toString()?.trim()?.lowercase(Locale.ROOT) ?: ""

        private fun idEntry(node: AccessibilityNodeInfo) =
            node.viewIdResourceName?.substringAfter(":id/") ?: ""

        private fun idTokens(rawId: String) =
            rawId.replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
                .lowercase(Locale.ROOT).split(Regex("[^a-z0-9]+")).filter { it.isNotEmpty() }.toSet()

        private fun matchesAny(s: String, words: List<String>): Boolean {
            if (s.isEmpty() || s.length > 40) return false
            return words.any { w ->
                if (s == w) return@any true
                if (w.all { it.code < 128 }) {
                    // latin words: whole-word match, e.g. "send", "send message", "tap to send"
                    Regex("(^|[^\\p{L}])${Regex.escape(w)}([^\\p{L}]|$)").containsMatchIn(s)
                } else {
                    // CJK etc.: substring match, e.g. "发送消息", "メッセージを送信"
                    s.contains(w)
                }
            }
        }

        private fun containsAny(s: String, parts: List<String>) = parts.any { s.contains(it) }

        private fun isImageOrButton(node: AccessibilityNodeInfo): Boolean {
            val cls = node.className?.toString() ?: return false
            return cls.contains("Image") || cls.contains("Button")
        }
    }

    sealed class Result {
        data class Clicked(val description: String) : Result()
        data class NotFound(val dump: String?) : Result()
        data object NoWindow : Result()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instanceRef = WeakReference(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // intentionally ignored: this service only acts on demand from the input method
    }

    override fun onInterrupt() {}

    override fun onUnbind(intent: Intent?): Boolean {
        instanceRef = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        instanceRef = null
        super.onDestroy()
    }

    /**
     * The window to search in. While the user is touching the keyboard, "active window" may be the
     * input method window, so look for the application window of [packageName] that holds input focus.
     */
    private fun findAppRoot(packageName: String): AccessibilityNodeInfo? {
        val roots = mutableListOf<AccessibilityNodeInfo>()
        runCatching {
            windows.filter { it.type == AccessibilityWindowInfo.TYPE_APPLICATION }.forEach { w ->
                val r = w.root ?: return@forEach
                if (r.packageName?.toString() == packageName) {
                    if (w.isFocused || w.isActive) roots.add(0, r) else roots.add(r)
                }
            }
        }
        rootInActiveWindow?.let { r ->
            if (r.packageName?.toString() == packageName) roots.add(r)
        }
        // prefer the window that contains the focused input
        return roots.firstOrNull { it.findFocus(AccessibilityNodeInfo.FOCUS_INPUT) != null }
            ?: roots.firstOrNull()
    }

    private fun isAncestorOrSelf(node: AccessibilityNodeInfo, of: AccessibilityNodeInfo?): Boolean {
        of ?: return false
        var cur: AccessibilityNodeInfo? = of
        var depth = 0
        while (cur != null && depth < 50) {
            if (cur == node) return true
            cur = cur.parent
            depth++
        }
        return false
    }

    private fun hasClickAction(node: AccessibilityNodeInfo) =
        node.isClickable || node.actionList.any { it.id == AccessibilityNodeInfo.ACTION_CLICK }

    /**
     * the node itself if it's clickable, otherwise its nearest clickable ancestor (up to 4 levels)
     */
    private fun clickableTarget(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        var cur: AccessibilityNodeInfo? = node
        var depth = 0
        while (cur != null && depth < 5) {
            if (hasClickAction(cur)) return cur
            cur = cur.parent
            depth++
        }
        return null
    }

    private class Ctx(
        val root: AccessibilityNodeInfo,
        val focused: AccessibilityNodeInfo?,
        val focusRect: Rect,
    ) {
        val hasFocus = focused != null && !focusRect.isEmpty
        val band = maxOf(focusRect.height(), 160)
    }

    /**
     * validate a candidate and return (clickable target, geometry bonus), or null if unusable
     */
    private fun checkCandidate(ctx: Ctx, node: AccessibilityNodeInfo): Pair<AccessibilityNodeInfo, Int>? {
        val target = clickableTarget(node) ?: return null
        target.refresh()
        if (!target.isVisibleToUser || !target.isEnabled || target.isEditable) return null
        // a container that holds the input box itself is not a button
        if (isAncestorOrSelf(target, ctx.focused)) return null
        val r = Rect().also { target.getBoundsInScreen(it) }
        if (r.isEmpty) return null
        var bonus = 0
        if (ctx.hasFocus) {
            val fr = ctx.focusRect
            // the send button sits in the same row as the input box, overlaps it, or is below it;
            // anything well above it is chat history or a toolbar, never click those
            if (r.centerY() < fr.top - ctx.band) return null
            val dy = abs(r.centerY() - fr.centerY())
            if (dy <= ctx.band || Rect.intersects(r, fr)) bonus += 30
            if (r.left >= fr.centerX()) bonus += 20
            bonus -= minOf(dy / 40, 60)
        }
        return target to bonus
    }

    private fun describe(node: AccessibilityNodeInfo): String {
        val id = idEntry(node)
        val desc = node.contentDescription?.toString()?.take(30) ?: ""
        return listOf(id, desc).filter { it.isNotEmpty() }.joinToString(" / ").ifEmpty {
            node.className?.toString()?.substringAfterLast('.') ?: "?"
        }
    }

    private fun click(target: AccessibilityNodeInfo): Result? =
        if (target.performAction(AccessibilityNodeInfo.ACTION_CLICK)) Result.Clicked(describe(target)) else null

    /** step 1: per-app rules, using direct (uncached) queries on the app side */
    private fun tryRule(ctx: Ctx, rule: AppRule, packageName: String): Result? {
        var best: AccessibilityNodeInfo? = null
        var bestScore = Int.MIN_VALUE
        fun consider(node: AccessibilityNodeInfo, base: Int) {
            val (target, bonus) = checkCandidate(ctx, node) ?: return
            if (base + bonus > bestScore) {
                bestScore = base + bonus
                best = target
            }
        }
        rule.ids.forEach { id ->
            runCatching { ctx.root.findAccessibilityNodeInfosByViewId("$packageName:id/$id") }
                .getOrNull()?.forEach { consider(it, 200) }
        }
        rule.labels.forEach { label ->
            runCatching { ctx.root.findAccessibilityNodeInfosByText(label) }.getOrNull()?.forEach { n ->
                if (n.isEditable) return@forEach
                val desc = norm(n.contentDescription)
                val text = norm(n.text).takeIf { it.length <= 16 } ?: ""
                if (desc == label || text == label) consider(n, 150)
            }
        }
        return best?.let { click(it) }
    }

    /** step 2: generic heuristic over the whole window */
    private fun tryGeneric(ctx: Ctx, rule: AppRule?): Result? {
        var best: AccessibilityNodeInfo? = null
        var bestScore = 0
        // for apps with unlabeled send icons: rightmost image button in the input row
        var rightmost: AccessibilityNodeInfo? = null
        var rightmostX = Int.MIN_VALUE
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(ctx.root)
        var visited = 0
        while (queue.isNotEmpty() && visited < MAX_NODES) {
            val node = queue.removeFirst()
            visited++
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
            if (!node.isVisibleToUser || node.isEditable) continue
            val text = norm(node.text)
            val desc = norm(node.contentDescription)
            val tokens = idTokens(idEntry(node))
            if (containsAny(text, NEGATIVE_PARTS) || containsAny(desc, NEGATIVE_PARTS)) continue
            if (tokens.any { it in NEGATIVE_ID_TOKENS }) continue
            // button labels are short; long texts are most likely chat messages
            val label = if (text.length <= 16) text else ""
            var score = 0
            if (matchesAny(label, SEND_WORDS) || matchesAny(desc, SEND_WORDS)) score += 100
            if (tokens.any { it in SEND_ID_TOKENS }) score += 80
            else if (tokens.any { it in POST_ID_TOKENS } && tokens.any { it in BUTTON_ID_TOKENS }) score += 60
            if (score == 0 && (matchesAny(label, POST_WORDS) || matchesAny(desc, POST_WORDS))) score += 40
            if (score == 0) {
                if (rule?.unlabeledRightmost == true && ctx.hasFocus && text.isEmpty() && desc.isEmpty() &&
                    isImageOrButton(node) && hasClickAction(node) && node.isEnabled
                ) {
                    val r = Rect().also { node.getBoundsInScreen(it) }
                    val fr = ctx.focusRect
                    if (abs(r.centerY() - fr.centerY()) <= ctx.band && r.left >= fr.right - r.width() &&
                        r.right > rightmostX && !isAncestorOrSelf(node, ctx.focused)
                    ) {
                        rightmostX = r.right
                        rightmost = node
                    }
                }
                continue
            }
            val (target, bonus) = checkCandidate(ctx, node) ?: continue
            score += bonus
            if (score > bestScore) {
                bestScore = score
                best = target
            }
        }
        best?.let { if (bestScore >= 60) click(it)?.let { r -> return r } }
        rightmost?.let { click(it)?.let { r -> return r } }
        return null
    }

    /**
     * Find and click the most likely "send" button in the window of [packageName].
     * @param diagnostic whether to build a dump of button-like nodes when nothing is found
     */
    fun clickSendButton(packageName: String, diagnostic: Boolean = false): Result {
        // make sure we don't look at a stale node cache (eg. a send button that was disabled
        // before the user typed, or didn't exist yet)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runCatching { clearCache() }
        }
        val root = findAppRoot(packageName) ?: return Result.NoWindow
        root.refresh()
        val focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)?.also { it.refresh() }
        val focusRect = Rect().also { r -> focused?.getBoundsInScreen(r) }
        val ctx = Ctx(root, focused, focusRect)
        val rule = RULES[packageName]
        rule?.let { tryRule(ctx, it, packageName) }?.let { return it }
        tryGeneric(ctx, rule)?.let { return it }
        return Result.NotFound(if (diagnostic) dump(ctx, packageName) else null)
    }

    /**
     * Diagnostic dump: ONLY clickable / image / button-like nodes in the bottom half of the window.
     * Never includes text of editable fields; other text only if it's at most 12 characters.
     */
    private fun dump(ctx: Ctx, packageName: String): String {
        val sb = StringBuilder()
        val rootRect = Rect().also { ctx.root.getBoundsInScreen(it) }
        val midY = rootRect.centerY()
        sb.append("fcitx5 Smart Enter diagnostic dump\n")
        sb.append("package=").append(packageName)
            .append(" sdk=").append(Build.VERSION.SDK_INT)
            .append(" window=").append(rootRect.toShortString()).append('\n')
        ctx.focused?.let {
            sb.append("focusedInput class=").append(it.className)
                .append(" id=").append(it.viewIdResourceName ?: "")
                .append(" bounds=").append(ctx.focusRect.toShortString()).append('\n')
        } ?: sb.append("focusedInput=none\n")
        sb.append("--- nodes (bottom half, clickable/image/button only) ---\n")
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(ctx.root)
        var visited = 0
        var count = 0
        while (queue.isNotEmpty() && visited < MAX_NODES && count < 300) {
            val node = queue.removeFirst()
            visited++
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
            if (node.isEditable) continue
            if (!(hasClickAction(node) || isImageOrButton(node))) continue
            val r = Rect().also { node.getBoundsInScreen(it) }
            if (r.isEmpty || r.bottom < midY) continue
            count++
            // labels are recorded only for button-like nodes or nodes in the input row,
            // so that chat bubbles (clickable, but above the input) never leak message content
            val inRow = ctx.hasFocus && abs(r.centerY() - ctx.focusRect.centerY()) <= ctx.band
            val labelOk = isImageOrButton(node) || inRow
            val text = if (labelOk) node.text?.toString()?.takeIf { it.length <= 12 } ?: "" else "(omitted)"
            val desc = if (labelOk) node.contentDescription?.toString()?.take(40) ?: "" else "(omitted)"
            sb.append("class=").append(node.className?.toString()?.substringAfterLast('.') ?: "")
                .append(" id=").append(node.viewIdResourceName ?: "")
                .append(" desc=").append(desc.replace('\n', ' '))
                .append(" text=").append(text.replace('\n', ' '))
                .append(" bounds=").append(r.toShortString())
                .append(" clickable=").append(hasClickAction(node))
                .append(" enabled=").append(node.isEnabled)
                .append(" visible=").append(node.isVisibleToUser)
                .append('\n')
        }
        return sb.toString()
    }
}
