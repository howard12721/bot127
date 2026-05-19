package jp.xhw.bot127.bot

import jp.xhw.bot127.bot.command.installCommands
import jp.xhw.trakt.bot.context.base.fetchChannelByPath
import jp.xhw.trakt.bot.context.bot.join
import jp.xhw.trakt.bot.infrastructure.runtime.TraktClientBuilder
import jp.xhw.trakt.bot.model.ChannelPath
import jp.xhw.trakt.bot.model.Initialized

fun TraktClientBuilder.configureBot(services: BotServices) {
    on<Initialized> {
        services.config.configChannelPath?.let { path ->
            fetchChannelByPath(ChannelPath(path))?.join()
        }
        services.activeUsers.refresh()
    }

    installCommands(services)
}
