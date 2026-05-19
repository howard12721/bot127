package jp.xhw.bot127.persistence

import jp.xhw.trakt.bot.model.ChannelId
import kotlin.uuid.Uuid

internal fun channelIdToStorage(channelId: ChannelId?): String = channelId?.value?.toString().orEmpty()

internal fun channelIdFromStorage(raw: String): ChannelId? =
    if (raw.isBlank()) {
        null
    } else {
        ChannelId(Uuid.parse(raw))
    }
