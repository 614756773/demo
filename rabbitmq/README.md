#### 该demo完成了`RabbitMQ`的六种消息模式，分别是
- simple：简单队列
- work：工作队列
- publish/subscribe：推送和订阅
- route：路由
- topic：主题
- rpc：模拟RPC
### 运行
- 前提条件
    - `Erlang`的[安装](http://www.erlang.org/download.html)
    - `RabbitMQ Server`的[安装](https://www.rabbitmq.com/install-windows.html#chocolatey)
- 启动`RabbitMQ Server`
    - `cd {your path}\rabbitmq_server-3.8.1\sbin` 
    - `rabbitmq-server.bat start`
    > 启动时可能会提醒你rabbitmq已经启动了，是因为在安装的时候选择了启动，<br>此时需要运行`rabbitmqctl.bat stop`先将服务关闭。
    
    > 在启动之前可以执行`rabbitmq-plugins.bat enable rabbitmq_management`开启网页控制台，<br>访问地址`localhost:15672`，账号和密码都为`guest`
- 运行各个demo
    - 在没有使用exchange的demo中，Sender和Recevier可以任意选择一个先执行
    - 如使用了exchange，则必须先运行Recevier，因为这种模式下的消息是即使的，不可堆积的