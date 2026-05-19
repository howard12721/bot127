package jp.xhw.bot127.config

import kotlin.uuid.Uuid

data class AppConfig(
    val botToken: String,
    val botId: Uuid,
    val userToken: String,
    val configChannelPath: String?,
    val pendingRuleSeeds: List<PendingRuleSeed>,
    val database: DatabaseConfig,
) {
    companion object {
        fun fromEnvironment(environment: Map<String, String> = System.getenv()): AppConfig {
            val botToken = environment.required("TRAQ_BOT_TOKEN")
            val botId =
                environment.required("TRAQ_BOT_ID").let { rawBotId ->
                    runCatching { Uuid.parse(rawBotId) }.getOrElse { cause ->
                        throw IllegalArgumentException("TRAQ_BOT_ID must be a valid UUID.", cause)
                    }
                }
            val userToken = environment.required("TRAQ_USER_TOKEN")
            val configChannelPath = environment.optional("FORWARD_CONFIG_CHANNEL")
            val pendingRuleSeeds = parsePendingRuleSeeds(environment.optional("FORWARD_RULES"))

            return AppConfig(
                botToken = botToken,
                botId = botId,
                userToken = userToken,
                configChannelPath = configChannelPath,
                pendingRuleSeeds = pendingRuleSeeds,
                database = DatabaseConfig.fromEnvironment(environment),
            )
        }
    }
}
