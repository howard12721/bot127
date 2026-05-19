package jp.xhw.bot127.domain

fun parseRegexPattern(raw: String): Regex {
    val trimmed = raw.trim()
    if (trimmed.startsWith('/') && trimmed.lastIndexOf('/') > 0) {
        val lastSlash = trimmed.lastIndexOf('/')
        val body = trimmed.substring(1, lastSlash)
        val flags = trimmed.substring(lastSlash + 1)
        val options =
            buildSet {
                if ('i' in flags) add(RegexOption.IGNORE_CASE)
                if ('m' in flags) add(RegexOption.MULTILINE)
            }
        return Regex(body, options)
    }

    return Regex(trimmed)
}
