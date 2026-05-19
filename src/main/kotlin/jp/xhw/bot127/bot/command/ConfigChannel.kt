package jp.xhw.bot127.bot.command

import jp.xhw.bot127.bot.ConfigChannelAccess
import jp.xhw.bot127.config.AppConfig
import jp.xhw.trakt.bot.context.base.fetchChannelByPath
import jp.xhw.trakt.bot.context.bot.BotContext
import jp.xhw.trakt.bot.context.bot.join
import jp.xhw.trakt.bot.model.ChannelPath
import jp.xhw.trakt.bot.model.Message

internal fun Message.Detail.isAllowedConfigChannel(access: ConfigChannelAccess): Boolean =
    access.allows(channel.id)

context(_: BotContext)
internal suspend fun resolveConfigChannelAccess(config: AppConfig): ConfigChannelAccess {
    val path =
        config.configChannelPath
            ?: return ConfigChannelAccess.unrestricted
    val channel =
        fetchChannelByPath(ChannelPath(path))
            ?: throw IllegalStateException(
                "FORWARD_CONFIG_CHANNEL で指定されたチャンネルが見つかりません: $path",
            )
    channel.join()
    return ConfigChannelAccess.restricted(channel.id)
}
