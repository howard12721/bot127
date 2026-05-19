package jp.xhw.bot127.bot

import jp.xhw.bot127.bot.command.installCommands
import jp.xhw.bot127.bot.command.resolveConfigChannelAccess
import jp.xhw.trakt.bot.infrastructure.runtime.TraktClientBuilder
import jp.xhw.trakt.bot.model.Initialized

fun TraktClientBuilder.configureBot(services: BotServices) {
    on<Initialized> {
        services.configChannelAccess = resolveConfigChannelAccess(services.config)
        services.activeUsers.refresh()
    }

    installCommands(services)
}
