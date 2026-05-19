package jp.xhw.bot127.persistence

import jp.xhw.bot127.domain.ForwardRule
import jp.xhw.bot127.persistence.table.ForwardRulesTable
import jp.xhw.trakt.bot.model.ChannelId
import jp.xhw.trakt.bot.model.UserId
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import kotlin.uuid.Uuid

class ForwardRuleRepository {
    suspend fun add(rule: ForwardRule): ForwardRule =
        dbQuery {
            ForwardRulesTable.insert {
                it[id] = rule.id.toString()
                it[channelId] = rule.channelId.value.toString()
                it[patternText] = rule.patternText
                it[targetUserId] = rule.targetUserId.value.toString()
            }
            rule
        }

    suspend fun remove(id: Uuid): ForwardRule? =
        dbQuery {
            val existing =
                ForwardRulesTable
                    .selectAll()
                    .where { ForwardRulesTable.id eq id.toString() }
                    .firstOrNull()
                    ?.toForwardRule()
                    ?: return@dbQuery null

            ForwardRulesTable.deleteWhere { ForwardRulesTable.id eq id.toString() }
            existing
        }

    suspend fun all(): List<ForwardRule> =
        dbQuery {
            ForwardRulesTable
                .selectAll()
                .orderBy(ForwardRulesTable.createdAt to SortOrder.ASC, ForwardRulesTable.id to SortOrder.ASC)
                .map { it.toForwardRule() }
        }

    suspend fun forChannel(channelId: ChannelId): List<ForwardRule> =
        dbQuery {
            ForwardRulesTable
                .selectAll()
                .where { ForwardRulesTable.channelId eq channelId.value.toString() }
                .orderBy(ForwardRulesTable.createdAt to SortOrder.ASC, ForwardRulesTable.id to SortOrder.ASC)
                .map { it.toForwardRule() }
        }

    suspend fun isEmpty(): Boolean =
        dbQuery {
            ForwardRulesTable.selectAll().empty()
        }

    private fun ResultRow.toForwardRule(): ForwardRule =
        ForwardRule(
            id = Uuid.parse(this[ForwardRulesTable.id]),
            channelId = ChannelId(Uuid.parse(this[ForwardRulesTable.channelId])),
            patternText = this[ForwardRulesTable.patternText],
            targetUserId = UserId(Uuid.parse(this[ForwardRulesTable.targetUserId])),
        )
}
