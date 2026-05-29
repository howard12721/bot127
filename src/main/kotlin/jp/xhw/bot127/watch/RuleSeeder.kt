package jp.xhw.bot127.watch

import jp.xhw.bot127.config.AppConfig
import jp.xhw.bot127.config.PendingRuleSeed
import jp.xhw.bot127.domain.ForwardRule
import jp.xhw.bot127.domain.isAnyChannelPath
import jp.xhw.bot127.persistence.CachingForwardRuleRepository
import jp.xhw.trakt.bot.context.base.fetchChannelByPath
import jp.xhw.trakt.bot.context.user.UserContext
import jp.xhw.trakt.bot.model.ChannelId
import jp.xhw.trakt.bot.model.ChannelPath

context(_: UserContext)
internal suspend fun seedRulesIfEmpty(
    config: AppConfig,
    repository: CachingForwardRuleRepository,
) {
    if (!repository.isEmpty()) {
        return
    }

    val failures = mutableListOf<String>()
    val resolved = mutableListOf<Pair<PendingRuleSeed, ChannelId?>>()
    for (seed in config.pendingRuleSeeds) {
        if (isAnyChannelPath(seed.channelPath)) {
            resolved += seed to null
            continue
        }
        val channel = fetchChannelByPath(ChannelPath.parse(seed.channelPath))
        if (channel == null) {
            failures += seed.channelPath
        } else {
            resolved += seed to channel.id
        }
    }
    if (failures.isNotEmpty()) {
        throw IllegalStateException(
            "FORWARD_RULES の初期投入に失敗しました。チャンネルが見つかりません: " +
                failures.joinToString(", "),
        )
    }
    for ((seed, channelId) in resolved) {
        repository.add(
            ForwardRule.create(
                channelId = channelId,
                patternText = seed.patternText,
                targetUserId = seed.targetUserId,
            ),
        )
    }
}
