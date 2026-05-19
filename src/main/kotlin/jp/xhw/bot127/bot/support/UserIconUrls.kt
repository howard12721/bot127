package jp.xhw.bot127.bot.support

import jp.xhw.trakt.bot.context.base.fetchFileMeta
import jp.xhw.trakt.bot.context.base.reply
import jp.xhw.trakt.bot.context.base.url
import jp.xhw.trakt.bot.context.bot.BotContext
import jp.xhw.trakt.bot.model.File
import jp.xhw.trakt.bot.model.Message

context(_: BotContext)
internal suspend fun File.Ref.toUrl(): String = fetchFileMeta(id).url()

context(_: BotContext)
internal suspend fun Message.Detail.replyUserIcon(
    userName: String,
    iconFile: File.Ref,
) {
    runCatching {
        reply(iconFile.toUrl(), embed = true)
    }.recoverCatching { error ->
        reply("@$userName のアイコンを取得できません: ${error.message ?: error::class.simpleName}")
    }
}
