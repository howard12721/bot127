package jp.xhw.bot127.bot.command

internal fun botHelpText(): String =
    """
    ## メッセージ転送BOT
    
    - `!forward add #channel /正規表現/`
      - 指定パブリックチャンネルのメッセージが正規表現にマッチしたら、コマンド送信者のDMにメッセージURLを送ります。
      - 末尾に `include-self` または `include-bot` を付けると、それぞれ自分・Bot のメッセージも転送対象に含めます（デフォルトは両方除外）。
    - `!forward add any /正規表現/`（`任意` または `*` も可）
      - 参加中の全パブリックチャンネルを転送元にします。
      DM チャンネルは監視・ルール登録の対象外です。
      正規表現は `/pattern/` または `/pattern/i`（大文字小文字無視）形式、または素のパターン文字列でも指定できます。
    
    - `!forward list`
      - 登録済みルール一覧を表示します。
    
    - `!forward set <ルールID> self|bot exclude|include`
      - 自分または Bot のメッセージを転送対象から除外するか変更します（`除外` / `含める` も可）。
    
    - `!forward remove <ルールID>`
      - 自分が作成したルールを削除します。ID は list コマンドで確認できます。
    
    - `!icon @user` / `!icon random`
      - 指定ユーザーまたはランダムなユーザーのアイコン画像 URL を投稿します。
    """.trimIndent() + "\n"
