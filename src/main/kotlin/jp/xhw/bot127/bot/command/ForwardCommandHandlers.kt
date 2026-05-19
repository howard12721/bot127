package jp.xhw.bot127.bot.command

import jp.xhw.bot127.bot.BotServices
import jp.xhw.bot127.domain.ForwardRule
import jp.xhw.bot127.domain.parseRegexPattern
import jp.xhw.trakt.bot.command.CommandContext
import jp.xhw.trakt.bot.context.base.fetchChannelOrNull
import jp.xhw.trakt.bot.context.base.fetchPath
import jp.xhw.trakt.bot.context.base.fetchUserOrNull
import jp.xhw.trakt.bot.context.base.reply
import jp.xhw.trakt.bot.context.bot.BotContext
import kotlin.uuid.Uuid

context(_: BotContext)
internal suspend fun CommandContext.handleForwardAdd(services: BotServices) {
    if (!message.isAllowedConfigChannel(services.config)) return

    val channel = args.channel("channel")
    val targetUser = args.user("user")
    val patternText = args.string("pattern")

    val pattern =
        runCatching { parseRegexPattern(patternText) }.getOrElse { error ->
            message.reply("正規表現が不正です: ${error.message ?: error::class.simpleName}")
            return
        }

    val rule =
        ForwardRule.create(
            channelId = channel.id,
            patternText = patternText,
            targetUserId = targetUser.id,
        )
    services.rules.add(rule)

    message.reply(
        """
        転送ルールを追加しました。
        チャンネル: ${channel.fetchPath().value}
        正規表現: `${pattern.pattern}`
        転送先: @${targetUser.name}
        ルールID: `${rule.id}`
        """.trimIndent(),
    )
}

context(_: BotContext)
internal suspend fun CommandContext.handleForwardList(services: BotServices) {
    if (!message.isAllowedConfigChannel(services.config)) return

    val rules = services.rules.all()
    if (rules.isEmpty()) {
        message.reply("登録されている転送ルールはありません。")
        return
    }

    val lines = rules.map { it.formatSummary() }
    message.reply("転送ルール一覧:\n${lines.joinToString("\n")}")
}

context(_: BotContext)
internal suspend fun CommandContext.handleForwardRemove(services: BotServices) {
    if (!message.isAllowedConfigChannel(services.config)) return

    val id =
        runCatching { Uuid.parse(args.string("ruleId")) }.getOrElse {
            message.reply("ルールIDは UUID 形式で指定してください。")
            return
        }

    val removed = services.rules.remove(id)
    if (removed == null) {
        message.reply("ルール `$id` は見つかりません。")
        return
    }

    message.reply("転送ルール `$id` を削除しました。")
}

context(_: BotContext)
internal suspend fun CommandContext.handleForwardHelp(services: BotServices) {
    if (!message.isAllowedConfigChannel(services.config)) return
    message.reply(botHelpText())
}

context(_: BotContext)
private suspend fun ForwardRule.formatSummary(): String {
    val channel = fetchChannelOrNull(channelId)
    val user = fetchUserOrNull(targetUserId)
    val channelLabel = channel?.fetchPath()?.value ?: channelId.value.toString()
    val userLabel = user?.name?.let { "@$it" } ?: targetUserId.value.toString()
    return "- `$channelLabel` `$patternText` -> $userLabel (id: `$id`)"
}
