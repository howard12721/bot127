package jp.xhw.bot127.domain

import jp.xhw.trakt.bot.context.base.BaseContext
import jp.xhw.trakt.bot.context.base.fetchChannelOrNull
import jp.xhw.trakt.bot.model.ChannelId

/** パブリックチャンネルとして取得できないチャンネル（DM 等）かどうか。 */
context(_: BaseContext)
suspend fun ChannelId.isDirectMessageChannel(): Boolean = fetchChannelOrNull(this) == null
