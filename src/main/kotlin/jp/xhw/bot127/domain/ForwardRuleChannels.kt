package jp.xhw.bot127.domain

const val ANY_CHANNEL_LABEL = "任意のチャンネル"

/** 転送元が全パブリックチャンネル対象であることを示すパス表記。 */
fun isAnyChannelPath(path: String): Boolean =
    when (path.trim()) {
        "*", "any", "任意" -> true
        else -> false
    }
