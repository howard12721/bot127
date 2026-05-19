package jp.xhw.bot127.bot.command

internal fun botHelpText(): String =
    """
    メッセージ転送BOT

    `!forward add #channel /正規表現/`
      指定パブリックチャンネルのメッセージが正規表現にマッチしたら、コマンド送信者のDMにメッセージURLを送ります。
    `!forward add any /正規表現/`（`任意` または `*` も可）
      参加中の全パブリックチャンネルを転送元にします。
      DM チャンネルは監視・ルール登録の対象外です。
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
