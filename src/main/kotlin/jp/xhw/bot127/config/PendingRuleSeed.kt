package jp.xhw.bot127.config

import jp.xhw.bot127.domain.parseRegexPattern
import jp.xhw.trakt.bot.model.UserId
import kotlin.uuid.Uuid

data class PendingRuleSeed(
    val channelPath: String,
    val patternText: String,
    val targetUserId: UserId,
)

internal fun parsePendingRuleSeeds(raw: String?): List<PendingRuleSeed> {
    if (raw.isNullOrBlank()) {
        return emptyList()
    }

    return raw.split(';').mapNotNull { entry ->
        val parts = entry.split('|', limit = 3)
        if (parts.size != 3) {
            return@mapNotNull null
        }

        val channelPath = parts[0].trim()
        val patternText = parts[1].trim()
        val targetUserRaw = parts[2].trim()
        if (channelPath.isBlank() || patternText.isBlank() || targetUserRaw.isBlank()) {
            return@mapNotNull null
        }

        runCatching {
            parseRegexPattern(patternText)
            PendingRuleSeed(
                channelPath = channelPath,
                patternText = patternText,
                targetUserId = UserId(Uuid.parse(targetUserRaw.removePrefix("@"))),
            )
        }.getOrNull()
    }
}
