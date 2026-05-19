package jp.xhw.bot127.domain

import jp.xhw.trakt.bot.model.UserId

data class ForwardAddOptions(
    val patternText: String,
    val excludeOwnMessages: Boolean,
    val excludeBotMessages: Boolean,
)

private val INCLUDE_SELF_SUFFIX = Regex("""\s+include-self$""")
private val INCLUDE_BOT_SUFFIX = Regex("""\s+include-bot$""")

fun parseForwardAddOptions(raw: String): ForwardAddOptions {
    var excludeOwn = true
    var excludeBot = true
    var remaining = raw.trim()
    while (true) {
        when {
            INCLUDE_BOT_SUFFIX.containsMatchIn(remaining) -> {
                excludeBot = false
                remaining = remaining.replace(INCLUDE_BOT_SUFFIX, "")
            }

            INCLUDE_SELF_SUFFIX.containsMatchIn(remaining) -> {
                excludeOwn = false
                remaining = remaining.replace(INCLUDE_SELF_SUFFIX, "")
            }

            else -> break
        }
    }
    return ForwardAddOptions(
        patternText = remaining.trim(),
        excludeOwnMessages = excludeOwn,
        excludeBotMessages = excludeBot,
    )
}

fun ForwardRule.shouldExcludeMessage(
    authorId: UserId,
    botUserId: UserId?,
): Boolean {
    if (excludeOwnMessages && authorId == targetUserId) {
        return true
    }
    if (excludeBotMessages && botUserId != null && authorId == botUserId) {
        return true
    }
    return false
}
