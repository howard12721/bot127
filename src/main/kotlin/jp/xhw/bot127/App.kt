package jp.xhw.bot127

import jp.xhw.bot127.bot.BotServices
import jp.xhw.bot127.bot.configureBot
import jp.xhw.bot127.config.AppConfig
import jp.xhw.bot127.persistence.DatabaseFactory
import jp.xhw.bot127.persistence.ForwardRuleRepository
import jp.xhw.bot127.watch.configureWatcher
import jp.xhw.trakt.bot.selfTrakt
import jp.xhw.trakt.bot.trakt
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object App {
    suspend fun run() {
        val config = AppConfig.fromEnvironment()
        DatabaseFactory.init(config.database)
        registerShutdownHook { DatabaseFactory.close() }

        val services =
            BotServices(
                config = config,
                rules = ForwardRuleRepository(),
            )

        val bot =
            trakt(token = config.botToken, botId = config.botId) {
                configureBot(services)
            }

        val selfBot =
            selfTrakt(token = config.userToken) {
                configureWatcher(services, bot)
            }

        coroutineScope {
            launch { bot.run() }
            selfBot.run()
        }
    }

    private fun registerShutdownHook(onShutdown: () -> Unit) {
        Runtime.getRuntime().addShutdownHook(Thread(onShutdown))
    }
}
