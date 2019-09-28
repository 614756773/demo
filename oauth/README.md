- [参考](http://www.ruanyifeng.com/blog/2019/04/github-oauth.html)
#### 运行条件
- 需要有公网ip`如果没有，可以使用内网穿透，下面有教程`
    - github回调我们的服务时，需要有一个公网ip地址才能访问嘛
- 配置你的github账号，添加一个oauth
    - 地址：https://github.com/settings/developers
    - 点击**New Oauth App**
    - 填写**Application name**`随意即可`
    - 填写**Homepage URL**`如http://www.qz-hotpot.xyz:433`
    - 填写**Application description**
    - 填写**Authorization callback URL**`如http://www.qz-hotpot.xyz:433/oauth/callback`
- 添加oauth完毕后可以在其页面找到Client ID和Client Secret，<BR>
将这两个值复制到GithubOauth类的clientId字段和clientSecret中
#### 运行步骤
- 运行Application#main
- 访问localhost:443/oauth
#### 其他
- [内网穿透教程](https://natapp.cn/article/natapp_newbie)