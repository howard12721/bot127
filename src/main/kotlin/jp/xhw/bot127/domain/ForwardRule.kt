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
    /** ルール作成者（転送先）のメッセージを転送しない。 */
    val excludeOwnMessages: Boolean = true,
    /** この Bot のメッセージを転送しない。 */
    val excludeBotMessages: Boolean = true,
) {
    val pattern: Regex by lazy { parseRegexPattern(patternText) }

    val matchesAnyChannel: Boolean
        get() = channelId == null

    companion object {
        fun create(
            channelId: ChannelId?,
            patternText: String,
            targetUserId: UserId,
            excludeOwnMessages: Boolean = true,
            excludeBotMessages: Boolean = true,
        ): ForwardRule =
            ForwardRule(
                id = Uuid.random(),
                channelId = channelId,
                patternText = patternText,
                targetUserId = targetUserId,
                excludeOwnMessages = excludeOwnMessages,
                excludeBotMessages = excludeBotMessages,
            )
    }
}
