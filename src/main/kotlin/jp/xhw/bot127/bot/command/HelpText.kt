package jp.xhw.bot127.bot.command

internal fun botHelpText(): String =
    """
    メッセージ転送BOT

    `!forward add #channel @user /正規表現/`
      指定チャンネルのメッセージが正規表現にマッチしたら、@user のDMにメッセージURLを送ります。
      正規表現は `/pattern/` または `/pattern/i`（大文字小文字無視）形式、または素のパターン文字列でも指定できます。

    `!forward list`
      登録済みルール一覧を表示します。

    `!forward remove <ルールID>`
      ルールを削除します。ID は list コマンドで確認できます。

    `!icon @user` / `!icon random`
      指定ユーザーまたはランダムなユーザーのアイコン画像 URL を投稿します。

    監視は Self Bot のタイムラインストリーム（UserMessageCreated）で行い、DM送信は Bot が担当します。
    転送ルールは MariaDB（Exposed）に永続化されます。
    Bot は設定チャンネルに参加している必要があります。
    """.trimIndent()
