package jp.xhw.bot127.domain

import jp.xhw.trakt.bot.model.ChannelId
import jp.xhw.trakt.bot.model.UserId
import kotlin.uuid.Uuid

data class ForwardRule(
    val id: Uuid,
    /** `null` のときは全パブリックチャンネルが転送元。 */
    val channelId: ChannelId?,
    val patternText: String,
    val targetUserId: UserId,
) {
    val pattern: Regex by lazy { parseRegexPattern(patternText) }

    val matchesAnyChannel: Boolean
        get() = channelId == null

    companion object {
        fun create(
            channelId: ChannelId?,
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
