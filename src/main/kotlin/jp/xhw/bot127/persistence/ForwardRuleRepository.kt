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
import org.jetbrains.exposed.v1.jdbc.update
import kotlin.uuid.Uuid

class ForwardRuleRepository {
    suspend fun add(rule: ForwardRule): ForwardRule =
        dbQuery {
            ForwardRulesTable.insert {
                it[id] = rule.id.toString()
                it[channelId] = channelIdToStorage(rule.channelId)
                it[patternText] = rule.patternText
                it[targetUserId] = rule.targetUserId.value.toString()
                it[excludeOwnMessages] = rule.excludeOwnMessages
                it[excludeBotMessages] = rule.excludeBotMessages
            }
            rule
        }

    suspend fun update(rule: ForwardRule): ForwardRule? =
        dbQuery {
            val existing =
                ForwardRulesTable
                    .selectAll()
                    .where { ForwardRulesTable.id eq rule.id.toString() }
                    .firstOrNull()
                    ?.toForwardRule()
                    ?: return@dbQuery null

            ForwardRulesTable.update({ ForwardRulesTable.id eq rule.id.toString() }) {
                it[excludeOwnMessages] = rule.excludeOwnMessages
                it[excludeBotMessages] = rule.excludeBotMessages
            }
            rule.copy(
                channelId = existing.channelId,
                patternText = existing.patternText,
                targetUserId = existing.targetUserId,
            )
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
            val channelRules =
                ForwardRulesTable
                    .selectAll()
                    .where { ForwardRulesTable.channelId eq channelId.value.toString() }
                    .orderBy(ForwardRulesTable.createdAt to SortOrder.ASC, ForwardRulesTable.id to SortOrder.ASC)
                    .map { it.toForwardRule() }
            val anyChannelRules =
                ForwardRulesTable
                    .selectAll()
                    .where { ForwardRulesTable.channelId eq "" }
                    .orderBy(ForwardRulesTable.createdAt to SortOrder.ASC, ForwardRulesTable.id to SortOrder.ASC)
                    .map { it.toForwardRule() }
            channelRules + anyChannelRules
        }

    suspend fun isEmpty(): Boolean =
        dbQuery {
            ForwardRulesTable.selectAll().empty()
        }

    private fun ResultRow.toForwardRule(): ForwardRule =
        ForwardRule(
            id = Uuid.parse(this[ForwardRulesTable.id]),
            channelId = channelIdFromStorage(this[ForwardRulesTable.channelId]),
            patternText = this[ForwardRulesTable.patternText],
            targetUserId = UserId(Uuid.parse(this[ForwardRulesTable.targetUserId])),
            excludeOwnMessages = this[ForwardRulesTable.excludeOwnMessages],
            excludeBotMessages = this[ForwardRulesTable.excludeBotMessages],
        )
}
