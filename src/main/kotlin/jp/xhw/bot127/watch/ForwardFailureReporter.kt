package jp.xhw.bot127.watch

import jp.xhw.bot127.bot.BotServices
import jp.xhw.trakt.bot.context.base.fetchChannelByPath
import jp.xhw.trakt.bot.context.base.sendMessage
import jp.xhw.trakt.bot.context.bot.BotContext
import jp.xhw.trakt.bot.model.ChannelPath
import jp.xhw.trakt.bot.model.UserId
import java.util.logging.Level
import java.util.logging.Logger

private val logger = Logger.getLogger("jp.xhw.bot127.watch.Forward")

context(_: BotContext)
internal suspend fun reportForwardFailure(
    services: BotServices,
    targetUserId: UserId,
    sourceMessageUrl: String,
    error: Throwable,
) {
    val reason = error.message ?: error::class.simpleName
    logger.log(
        Level.WARNING,
        "Failed to forward message to $targetUserId (source: $sourceMessageUrl): $reason",
        error,
    )

    val path = services.config.configChannelPath ?: return
    val notification =
        """
        転送に失敗しました。
        転送先: `$targetUserId`
        元メッセージ: $sourceMessageUrl
        原因: $reason
        """.trimIndent()

    runCatching {
        fetchChannelByPath(ChannelPath.parse(path))?.sendMessage(notification)
    }.onFailure { notifyError ->
        logger.log(
            Level.WARNING,
            "Failed to notify config channel about forward failure (channel: $path)",
            notifyError,
        )
    }
}
