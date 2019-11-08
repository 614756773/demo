## 运行前提
- 安装redis
- 启动redis-server`端口默认为6379,不设置密码`
> 可以启动一个redis-client订阅`subChannel`，方便观察消息推送的情况
## 运行步骤
- 启动Application
- 推送消息
    - 方式一：运行RedisPushTest
    - 方式二：使用redis-client推送消息`{"name":"小茗同学","age":"22""}`到`subChannel`
## 参考
[spring boot 官方](https://spring.io/guides/gs/messaging-redis/)