package jp.xhw.bot127.watch

import jp.xhw.bot127.config.AppConfig
import jp.xhw.bot127.domain.ForwardRule
import jp.xhw.bot127.persistence.ForwardRuleRepository
import jp.xhw.trakt.bot.context.base.fetchChannelByPath
import jp.xhw.trakt.bot.context.user.UserContext
import jp.xhw.trakt.bot.model.ChannelPath

context(_: UserContext)
internal suspend fun seedRulesIfEmpty(
    config: AppConfig,
    repository: ForwardRuleRepository,
) {
    if (!repository.isEmpty()) {
        return
    }

    config.pendingRuleSeeds.forEach { seed ->
        val channel = fetchChannelByPath(ChannelPath(seed.channelPath)) ?: return@forEach
        repository.add(
            ForwardRule.create(
                channelId = channel.id,
                patternText = seed.patternText,
                targetUserId = seed.targetUserId,
            ),
        )
    }
}
