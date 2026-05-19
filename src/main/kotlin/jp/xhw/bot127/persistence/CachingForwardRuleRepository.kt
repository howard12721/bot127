package jp.xhw.bot127.persistence

import jp.xhw.bot127.domain.ForwardRule
import jp.xhw.trakt.bot.model.ChannelId
import kotlin.uuid.Uuid
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * [ForwardRuleRepository] の読み取りをメモリに載せ、ホットパス（タイムライン監視）の DB アクセスを避ける。
 * 書き込みは DB を正とし、成功後にスナップショットを更新する。
 */
class CachingForwardRuleRepository(
    private val delegate: ForwardRuleRepository = ForwardRuleRepository(),
) {
    @Volatile
    private var snapshot: Snapshot = Snapshot.EMPTY

    private val writeLock = Mutex()

    suspend fun warmUp() {
        snapshot = Snapshot.from(delegate.all())
    }

    fun forChannel(channelId: ChannelId): List<ForwardRule> =
        snapshot.anyChannelRules + snapshot.byChannel[channelId].orEmpty()

    suspend fun add(rule: ForwardRule): ForwardRule {
        val added = delegate.add(rule)
        writeLock.withLock {
            snapshot = snapshot.withAdded(added)
        }
        return added
    }

    suspend fun remove(id: Uuid): ForwardRule? {
        val removed = delegate.remove(id) ?: return null
        writeLock.withLock {
            snapshot = snapshot.withRemoved(removed)
        }
        return removed
    }

    fun all(): List<ForwardRule> = snapshot.all

    fun isEmpty(): Boolean = snapshot.all.isEmpty()

    private data class Snapshot(
        val all: List<ForwardRule>,
        val byChannel: Map<ChannelId, List<ForwardRule>>,
        val anyChannelRules: List<ForwardRule>,
    ) {
        fun withAdded(rule: ForwardRule): Snapshot = from(all + rule)

        fun withRemoved(rule: ForwardRule): Snapshot = from(all.filter { it.id != rule.id })

        companion object {
            val EMPTY = Snapshot(emptyList(), emptyMap(), emptyList())

            fun from(rules: List<ForwardRule>): Snapshot {
                val (anyChannelRules, channelRules) = rules.partition { it.matchesAnyChannel }
                return Snapshot(
                    all = rules,
                    byChannel = channelRules.groupBy { it.channelId!! },
                    anyChannelRules = anyChannelRules,
                )
            }
        }
    }
}
