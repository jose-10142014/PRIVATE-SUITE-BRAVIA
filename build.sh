#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
: "${ANDROID_HOME:?Set ANDROID_HOME to an Android SDK containing platform 35 and build-tools 35.0.0}"
bt="$ANDROID_HOME/build-tools/35.0.0"
jar="$ANDROID_HOME/platforms/android-35/android.jar"
mkdir -p build/classes build/dex build/gen
"$bt/aapt2" compile --dir app/src/main/res -o build/resources.zip
"$bt/aapt2" link -o build/base.apk -I "$jar" --manifest app/src/main/AndroidManifest.xml -A app/src/main/assets --java build/gen build/resources.zip
find app/src/main/java build/gen -name '*.java' > build/sources.txt
if command -v javac >/dev/null; then
 javac -encoding UTF-8 -source 8 -target 8 -bootclasspath "$jar:$bt/core-lambda-stubs.jar" -d build/classes @build/sources.txt
else
 java -jar "${ECJ_JAR:?Install a JDK or set ECJ_JAR}" -encoding UTF-8 -source 8 -target 8 -bootclasspath "$jar:$bt/core-lambda-stubs.jar" -d build/classes @build/sources.txt
fi
find build/classes -name '*.class' > build/classes.txt
"$bt/d8" --lib "$jar" --min-api 23 --output build/dex @build/classes.txt
cp build/base.apk build/unsigned.apk
(cd build/dex && zip -q ../unsigned.apk classes.dex)
"$bt/zipalign" -f 4 build/unsigned.apk build/aligned.apk
if [ ! -f preview.keystore ]; then
 keytool -genkeypair -keystore preview.keystore -storepass android -keypass android -alias preview -dname 'CN=PRIVATE SUITE Preview' -keyalg RSA -keysize 2048 -validity 10000
fi
"$bt/apksigner" sign --ks preview.keystore --ks-pass pass:android --key-pass pass:android --out build/PRIVATE_SUITE_Android_preview.apk build/aligned.apk
"$bt/apksigner" verify --verbose build/PRIVATE_SUITE_Android_preview.apk
