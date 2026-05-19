package jp.xhw.bot127.persistence.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.CurrentTimestamp
import org.jetbrains.exposed.v1.javatime.timestamp

object ForwardRulesTable : Table("forward_rules") {
    val id = varchar("id", 36)
    val channelId = varchar("channel_id", 36)
    val patternText = varchar("pattern_text", 1000)
    val targetUserId = varchar("target_user_id", 36)
    val excludeOwnMessages = bool("exclude_own_messages")
    val excludeBotMessages = bool("exclude_bot_messages")
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)

    init {
        index(isUnique = false, channelId)
    }
}
