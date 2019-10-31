
### websocket消息推送
#### 方式一：使用stomp协议
- 启动流程
    - 运行Appliction
    - 访问localhost
    - 点击Connect
    - 发送消息`服务端就会收到消息，同时返回一个消息`
    - 新打开标签页`http://localhost:8080/demo1/push?str=你好` 此时就会主动将这条消息推送到客户端
#### 方式二：使用websocket协议
- 启动流程
    - 运行Application
    - 访问http://coolaf.com/tool/chattest
    - 在其测试连接地址中输入ws://127.0.0.1:8080/websocket/123
    - 点击连接
    - 发送消息`服务端就会收到消息，同时返回一个消息`
    - 新打开一个标签页`http://127.0.0.1:8080/demo2/push/123?msg=你好` 此时会推送消息给客户端