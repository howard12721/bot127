package jp.xhw.bot127.bot.command

import jp.xhw.bot127.bot.BotServices
import jp.xhw.bot127.bot.support.replyUserIcon
import jp.xhw.trakt.bot.command.CommandContext
import jp.xhw.trakt.bot.context.base.reply
import jp.xhw.trakt.bot.context.bot.BotContext

context(_: BotContext)
internal suspend fun CommandContext.handleIconRandom(services: BotServices) {
    val user =
        services.activeUsers.randomOrNull()
            ?: run {
                message.reply("ランダムに選べるユーザーが見つかりません。")
                return
            }

    message.replyUserIcon(user.name, user.iconFile)
}

context(_: BotContext)
internal suspend fun CommandContext.handleIconUser() {
    val user = args.user("user")
    message.replyUserIcon(user.name, user.iconFile)
}
