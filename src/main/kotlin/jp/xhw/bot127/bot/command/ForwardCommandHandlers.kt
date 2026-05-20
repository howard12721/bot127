package jp.xhw.bot127.bot.command

import jp.xhw.bot127.bot.BotServices
import jp.xhw.bot127.domain.*
import jp.xhw.trakt.bot.command.CommandContext
import jp.xhw.trakt.bot.context.base.fetchChannelOrNull
import jp.xhw.trakt.bot.context.base.fetchPath
import jp.xhw.trakt.bot.context.base.fetchUserOrNull
import jp.xhw.trakt.bot.context.base.reply
import jp.xhw.trakt.bot.context.bot.BotContext
import jp.xhw.trakt.bot.model.ChannelId
import kotlin.uuid.Uuid

context(_: BotContext)
internal suspend fun CommandContext.handleForwardAdd(
    services: BotServices,
    sourceChannelId: ChannelId?,
) {
    if (!message.isAllowedConfigChannel(services.configChannelAccess)) return

    if (sourceChannelId?.isDirectMessageChannel() == true) {
        message.reply("DM チャンネルは転送ルールの対象にできません。パブリックチャンネルを指定してください。")
        return
    }

    val targetUserId = message.author.id
    val addOptions = parseForwardAddOptions(args.string("pattern"))
    val patternText = addOptions.patternText

    if (patternText.isBlank()) {
        message.reply("正規表現を指定してください。")
        return
    }

    runCatching { parseRegexPattern(patternText) }.onFailure { error ->
        message.reply("正規表現が不正です: ${error.message ?: error::class.simpleName}")
        return
    }

    val rule =
        ForwardRule.create(
            channelId = sourceChannelId,
            patternText = patternText,
            targetUserId = targetUserId,
            excludeOwnMessages = addOptions.excludeOwnMessages,
            excludeBotMessages = addOptions.excludeBotMessages,
        )
    services.rules.add(rule)

    val targetUserName = fetchUserOrNull(targetUserId)?.name ?: targetUserId.value.toString()

    val channelLabel =
        if (sourceChannelId == null) {
            ANY_CHANNEL_LABEL
        } else {
            fetchChannelOrNull(sourceChannelId)?.fetchPath()?.value
                ?: sourceChannelId.value.toString()
        }

    message.reply(
        """
        転送ルールを追加しました。
        チャンネル: $channelLabel
        正規表現: `$patternText`
        転送先: @$targetUserName
        自分のメッセージ: ${rule.excludeOwnMessages.toExclusionLabel()}
        Bot のメッセージ: ${rule.excludeBotMessages.toExclusionLabel()}
        ルールID: `${rule.id}`
        """.trimIndent(),
    )
}

context(_: BotContext)
internal suspend fun CommandContext.handleForwardSet(services: BotServices) {
    if (!message.isAllowedConfigChannel(services.configChannelAccess)) return

    val id =
        runCatching { Uuid.parse(args.string("ruleId")) }.getOrElse {
            message.reply("ルールIDは UUID 形式で指定してください。")
            return
        }

    val rule = services.rules.all().find { it.id == id }
    if (rule == null) {
        message.reply("ルール `$id` は見つかりません。")
        return
    }

    if (rule.targetUserId != message.author.id) {
        message.reply("自分が作成したルールのみ変更できます。")
        return
    }

    val target = args.string("target")
    val mode = args.string("mode")
    val exclude =
        when (mode) {
            "exclude", "on", "除外" -> {
                true
            }

            "include", "off", "含める" -> {
                false
            }

            else -> {
                message.reply("モードは `exclude` / `include`（または `除外` / `含める`）で指定してください。")
                return
            }
        }

    val updatedRule =
        when (target) {
            "self", "own", "自分" -> {
                rule.copy(excludeOwnMessages = exclude)
            }

            "bot", "Bot" -> {
                rule.copy(excludeBotMessages = exclude)
            }

            else -> {
                message.reply("対象は `self`（自分）または `bot` で指定してください。")
                return
            }
        }

    services.rules.update(updatedRule)
    message.reply(
        """
        転送ルール `$id` を更新しました。
        自分のメッセージ: ${updatedRule.excludeOwnMessages.toExclusionLabel()}
        Bot のメッセージ: ${updatedRule.excludeBotMessages.toExclusionLabel()}
        """.trimIndent(),
    )
}

private fun Boolean.toExclusionLabel(): String = if (this) "除外" else "含める"

context(_: BotContext)
internal suspend fun CommandContext.handleForwardList(services: BotServices) {
    if (!message.isAllowedConfigChannel(services.configChannelAccess)) return

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
    if (!message.isAllowedConfigChannel(services.configChannelAccess)) return

    val id =
        runCatching { Uuid.parse(args.string("ruleId")) }.getOrElse {
            message.reply("ルールIDは UUID 形式で指定してください。")
            return
        }

    val rule = services.rules.all().find { it.id == id }
    if (rule == null) {
        message.reply("ルール `$id` は見つかりません。")
        return
    }

    if (rule.targetUserId != message.author.id) {
        message.reply("自分が作成したルールのみ削除できます。")
        return
    }

    services.rules.remove(id)
    message.reply("転送ルール `$id` を削除しました。")
}

context(_: BotContext)
internal suspend fun CommandContext.handleForwardHelp(services: BotServices) {
    if (!message.isAllowedConfigChannel(services.configChannelAccess)) return
    message.reply(botHelpText())
}

context(_: BotContext)
private suspend fun ForwardRule.formatSummary(): String {
    val channelLabel =
        when (val id = channelId) {
            null -> {
                ANY_CHANNEL_LABEL
            }

            else -> {
                val channel = fetchChannelOrNull(id)
                channel?.fetchPath()?.value ?: id.value.toString()
            }
        }
    val user = fetchUserOrNull(targetUserId)
    val userLabel = user?.name?.let { "@$it" } ?: targetUserId.value.toString()
    return "- `$channelLabel` `$patternText` -> $userLabel (id: `$id`, 自分:${excludeOwnMessages.toExclusionLabel()}, bot:${excludeBotMessages.toExclusionLabel()})"
}
