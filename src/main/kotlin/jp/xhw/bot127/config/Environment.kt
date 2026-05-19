package jp.xhw.bot127.config

internal fun Map<String, String>.required(key: String): String =
    this[key]?.takeIf(String::isNotBlank) ?: throw IllegalStateException("$key is required.")

internal fun Map<String, String>.optional(key: String): String? = this[key]?.takeIf(String::isNotBlank)
