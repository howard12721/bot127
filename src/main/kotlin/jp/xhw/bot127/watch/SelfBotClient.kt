package jp.xhw.bot127.watch

import jp.xhw.bot127.bot.BotServices
import jp.xhw.bot127.domain.isDirectMessageChannel
import jp.xhw.bot127.domain.shouldExcludeMessage
import jp.xhw.trakt.bot.context.base.fetch
import jp.xhw.trakt.bot.context.base.fetchUserOrNull
import jp.xhw.trakt.bot.context.base.sendDirectMessage
import jp.xhw.trakt.bot.context.base.url
import jp.xhw.trakt.bot.context.bot.BotContext
import jp.xhw.trakt.bot.context.user.UserContext
import jp.xhw.trakt.bot.context.user.setTimelineStreaming
import jp.xhw.trakt.bot.infrastructure.runtime.Runtime
import jp.xhw.trakt.bot.infrastructure.runtime.SelfTraktClientBuilder
import jp.xhw.trakt.bot.model.BotEvent
import jp.xhw.trakt.bot.model.Initialized
import jp.xhw.trakt.bot.model.UserMessageCreated

fun SelfTraktClientBuilder.configureWatcher(
    services: BotServices,
    bot: Runtime<BotContext, BotEvent>,
) {
    on<Initialized> {
        setTimelineStreaming(true)
        seedRulesIfEmpty(services.config, services.rules)
    }

    on<UserMessageCreated> { event ->
        forwardMatchingRules(event, services, bot)
    }
}

context(_: UserContext)
private suspend fun forwardMatchingRules(
    event: UserMessageCreated,
    services: BotServices,
    bot: Runtime<BotContext, BotEvent>,
) {
    val message = event.message.fetch()
    if (message.channel.id.isDirectMessageChannel()) {
        return
    }

    val authorIsBot = message.author.fetch().isBot
    val rules =
        services.rules
            .forChannel(message.channel.id)
            .filter { it.pattern.containsMatchIn(message.content) }
            .filter { !it.shouldExcludeMessage(message.author.id, authorIsBot) }
    if (rules.isEmpty()) {
        return
    }

    val content = message.content
    val messageUrl = message.url()

    rules
        .map { it.targetUserId }
        .distinct()
        .forEach { targetUserId ->
            bot.execute {
                val targetUser = fetchUserOrNull(targetUserId) ?: return@execute
                val failure =
                    runCatching {
                        targetUser.sendDirectMessage(content = messageUrl, embed = true)
                    }.exceptionOrNull()
                if (failure != null) {
                    reportForwardFailure(services, targetUserId, messageUrl, failure)
                }
            }
        }
}
