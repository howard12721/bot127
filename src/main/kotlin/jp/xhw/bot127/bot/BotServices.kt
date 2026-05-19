package jp.xhw.bot127.bot

import jp.xhw.bot127.bot.support.ActiveUserCache
import jp.xhw.bot127.config.AppConfig
import jp.xhw.bot127.persistence.CachingForwardRuleRepository

data class BotServices(
    val config: AppConfig,
    val rules: CachingForwardRuleRepository,
    val activeUsers: ActiveUserCache = ActiveUserCache(),
    var configChannelAccess: ConfigChannelAccess = ConfigChannelAccess.unrestricted,
)
