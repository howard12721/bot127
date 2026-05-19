package jp.xhw.bot127.bot.support

import jp.xhw.trakt.bot.context.base.fetchUsers
import jp.xhw.trakt.bot.context.bot.BotContext
import jp.xhw.trakt.bot.model.User

class ActiveUserCache {
    private var users: List<User.Basic> = emptyList()

    context(_: BotContext)
    suspend fun refresh() {
        users =
            fetchUsers(includeSuspended = false)
                .filter { !it.isBot }
    }

    fun randomOrNull(): User.Basic? = users.randomOrNull()
}
