# PRIVATE SUITE Android 共通APK・動作確認版

元ファイル: TV_Launcher_Chrome_v6_1_Portable_v6_0_SafeLite.zip
作成日: 2026-10-14

## 対象

Android 6.0 / API 23以上のスマートフォン、タブレット、Android TV、Google TV。
Fire TVはAndroidベースのFire OS搭載機で、API 23以上が対象です。
Vega OS搭載Fire TV（例: Fire TV Stick 4K Select）はAPKに対応せず、別版が必要です。
端末のWebView実装・バージョンや外部アプリにより動作差があります。

## インストール

1. PRIVATE_SUITE_Android_preview.apk を端末へコピーします。
2. 端末の設定で、このAPKを開くアプリからのインストールを許可します。
3. APKを開き、PRIVATE SUITEをインストールして起動します。
4. 動画再生にはVLCなどの対応プレーヤーを別途インストールしてください。

ADBを既に使える場合は、PCから次のコマンドでインストールできます。

```
adb install -r PRIVATE_SUITE_Android_preview.apk
```

テレビのアプリ一覧にも、スマートフォンのアプリ一覧にも表示される構成です。
通常アプリとして起動します。標準ホーム画面の置換や電源投入時の自動起動は実装していません。

## 操作

- タッチ、マウス、リモコンの十字キーと決定に対応。
- MODEボタンでLUXURY / HOTEL VODを切り替えます。
- 戻るボタンでメニューへ。トップ画面では終了確認を表示します。
- YouTube、TVer、Prime Videoは対応アプリの起動を試み、見つからなければブラウザで開きます。
- 外部アプリからの戻り方はそのアプリの仕様にも依存します。
- ライブTVは元のチャンネル一覧を引き継ぎ、VLCなどへ再生を渡します。
- 「端末の動画を開く」で動画ファイルを選択できます。
- 「M3Uを読み込む」で一覧を置き換えます。UTF-8、2MB以内、HTTP/HTTPSの絶対URLが対象です。「一覧を戻す」で同梱一覧に戻ります。
- 「URLで再生」はファイル選択画面がないテレビ端末でも利用できます。
- さいたま市北区の天気、BGM、効果音を引き継いでいます。

## この初版の制約

- 実機・エミュレーターでの起動、リモコン操作、配信再生は未確認です。APKのビルド、署名、マニフェスト、JavaScriptの構文を確認済みです。
- ブラウザを取得できなかったため、画面の自動レンダリング検証も未実施です。
- Windows専用のQRスマホリモコンサーバー、フォルダー自動走査、セットアップ処理は未移植です。
- 元の配信URLはそのまま収録しています。現在の配信状況や再生可否は確認していません。
- フォルダー内動画の自動一覧化はなく、ファイル選択からの再生です。
- Fire TVなどでファイル選択アプリがない場合は、URL再生をご利用ください。
- 元版のR18簡易ロックも引き継いでいます。初期値1234はソース内固定で、強固なアクセス制限ではありません。

## ソースからビルド

Android Studioでこのフォルダーを開くか、JDKとAndroid SDKを用意して build.sh を実行します。
SDK platform 35、build-tools 35.0.0を使用します。GradleプロジェクトはAGP 8.7.3 / Gradle 8.9 / JDK 17を想定しています。Gradleラッパーは同梱していません。

```sh
ANDROID_HOME=/path/to/android-sdk bash build.sh
```

配布APKは個人用試作署名です。同梱preview.keystore（別名preview、パスワードandroid）を保持すると、この試作版への上書き更新が可能です。この公開試作用鍵をストア配布や本番製品に使わないでください。
ライセンス表示は THIRD_PARTY_LICENSES.txt を参照してください。
