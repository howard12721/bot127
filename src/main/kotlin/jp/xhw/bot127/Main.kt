package example.traktbot

import example.traktbot.features.icongacha.IconGachaHandler
import jp.xhw.trakt.bot.trakt

suspend fun main() {
    val config = Config.Bot.fromEnvironment()

    val iconGacha = IconGachaHandler()

    val client =
        trakt(
            token = config.token,
            botId = config.botId,
        ) {
            with(iconGacha) {
                register()
            }
        }

    client.run()
}
