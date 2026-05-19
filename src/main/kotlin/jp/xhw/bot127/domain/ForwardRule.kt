package jp.xhw.bot127.domain

import jp.xhw.trakt.bot.model.ChannelId
import jp.xhw.trakt.bot.model.UserId
import kotlin.uuid.Uuid

data class ForwardRule(
    val id: Uuid,
    val channelId: ChannelId,
    val patternText: String,
    val targetUserId: UserId,
) {
    val pattern: Regex by lazy { parseRegexPattern(patternText) }

    companion object {
        fun create(
            channelId: ChannelId,
            patternText: String,
            targetUserId: UserId,
        ): ForwardRule =
            ForwardRule(
                id = Uuid.random(),
                channelId = channelId,
                patternText = patternText,
                targetUserId = targetUserId,
            )
    }
}
