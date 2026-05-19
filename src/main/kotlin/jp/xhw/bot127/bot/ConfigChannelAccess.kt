package jp.xhw.bot127.bot

import jp.xhw.bot127.bot.ConfigChannelAccess.Companion.unrestricted
import jp.xhw.trakt.bot.model.ChannelId

/**
 * `FORWARD_CONFIG_CHANNEL` の解決結果。
 * パス未設定時は [unrestricted]（全チャンネルで forward コマンドを受け付ける）。
 */
class ConfigChannelAccess private constructor(
    val restrictedChannelId: ChannelId?,
) {
    fun allows(channelId: ChannelId): Boolean = restrictedChannelId == null || restrictedChannelId == channelId

    companion object {
        val unrestricted = ConfigChannelAccess(restrictedChannelId = null)

        fun restricted(channelId: ChannelId): ConfigChannelAccess = ConfigChannelAccess(restrictedChannelId = channelId)
    }
}
