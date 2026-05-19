package jp.xhw.bot127.bot.command

import jp.xhw.bot127.config.AppConfig
import jp.xhw.trakt.bot.context.base.fetchChannelByPath
import jp.xhw.trakt.bot.context.bot.BotContext
import jp.xhw.trakt.bot.model.ChannelPath
import jp.xhw.trakt.bot.model.Message

context(_: BotContext)
internal suspend fun Message.Detail.isAllowedConfigChannel(config: AppConfig): Boolean {
    val path = config.configChannelPath ?: return true
    val configChannel = fetchChannelByPath(ChannelPath(path)) ?: return false
    return channel.id == configChannel.id
}
