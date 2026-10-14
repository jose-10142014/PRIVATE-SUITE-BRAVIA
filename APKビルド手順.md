# PRIVATE SUITE v1.2 BRAVIA / Android 9

**このZIPにはAPKは含まれていません。** Android SDKがあるPC、またはGitHub Actionsでビルドできます。

## GitHub Actionsでビルドする場合
1. 新しいプライベートGitHubリポジトリを用意します。
2. このZIPの `PRIVATE_SUITE_Android/` **内のファイル・フォルダ全部**を、リポジトリの最上位へアップロードします（`.github/workflows/build-apk.yml` も含む）。
3. GitHubの `Actions` → `Build PRIVATE SUITE BRAVIA APK` → `Run workflow` を選びます。
4. 完了後 `Artifacts` → `PRIVATE_SUITE_BRAVIA_Android9_debug_APK` からZIPを取り、 `app-debug.apk` をUSBメモリーへコピーします。
5. FW-55BZ35FでAndroid APKのインストールを許可し、ファイルマネージャ等で開きます。初回は端末側でインストール許可が必要な場合があります。

## PCでビルドする場合
JDK 17、Android SDK 35（platformとbuild-tools）、Gradle 8.9が必要です。プロジェクト直下で `gradle assembleDebug` を実行します。

## 重要
- テレビに設定した **Web版の起動URL** と、今回の **APK版** は別です。Proモードの起動アプリ指定がURLのままなら従来版が起動します。APKへの起動切替はテレビ側で別途設定・確認が必要です。
- YouTube・TVer・ABEMA・Netflix・Prime Videoは各公式アプリがインストールされている場合にアプリ起動を試みます。
- LIVE TVはチャンネル一覧の初期表示とリモコンフォーカスを調整。動画のソースURLの配信権・接続性を保証しません。
- Uber EatsはTV内の外部ブラウザが対応していないと利用できません。
- デバッグ署名APKです。将来の正式版へ更新する場合、署名鍵が異なると再インストールが必要になります。
- 初期化や標準ホーム置換はしません。先にAPKを手動起動して動作確認してください。
