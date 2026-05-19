package jp.xhw.bot127.bot

import jp.xhw.bot127.bot.support.ActiveUserCache
import jp.xhw.bot127.config.AppConfig
import jp.xhw.bot127.persistence.ForwardRuleRepository

data class BotServices(
    val config: AppConfig,
    val rules: ForwardRuleRepository,
    val activeUsers: ActiveUserCache = ActiveUserCache(),
)
