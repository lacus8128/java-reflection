# java-reflection
practice of reflection and building a web app with Java Servlet


## 目的  
リフレクションを使って実際にフレームワーク開発を実践すること。  

## 基盤（言語、DBMSなど）
* Java
* JSP/Java Servlet
* MySQL
* フロントエンド側では jQuery による Ajax を実装する（予定）
* フロントエンド側には管理画面テンプレート "AdminLTE" を実装する（予定）

## 処理の共通化
フレームワークの仕組みによって、下記が共通化される。

* リクエストパラメータの受け取り
* JavaBeans の呼び出し
* DB関係の処理の一部

結果として、アプリケーション固有の処理（"Service"）の開発に集中できるようになる。  

## 概観
* フロントエンド側から Ajax で各種パラメータを送信
* 最初に WebFilter が呼ばれ、処理はすぐに ServiceInvoker に委譲される
* ServiceInvoker はリクエストパラメータを受け取る
* ServiceInvoker は指定された Service を呼び出してパラメータを渡す
* ApplicationContext という名称の、DIコンテナを用意
* DB の処理を効率化するためのユーティリティクラスを用意
* Ajax と通常の通信の両方に対応できるようにアノテーション付与で制御可能

## 特記事項
日々の支出を記録する帳簿アプリケーションを想定したものとなっている。
