# ベースイメージ（OpenJDK 17）
FROM openjdk:17-jdk-slim

# 作業ディレクトリを設定
WORKDIR /app

# JARファイルをコンテナにコピー
COPY target/kajimate-*.jar app.jar

# ポートを開放
EXPOSE 8080

# アプリケーション起動コマンド
ENTRYPOINT ["java", "-jar", "app.jar"]