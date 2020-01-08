### 启动
- 运行Application
### package说明
- com.hotpot.ioc.annotation
    - 该包有两个注解，作用和Spring中的相同
- com.hotpot.ioc.test
    - 该包中为测试用的类
    - 为了模拟bean冲突我编写了`StudentServiceImpl`类和`StudentServiceImpl2`类，<br>
      当`StudentServiceImpl2`类上的`@component`注解被启用时，启动程序就会抛出一个异常，提示bean冲突
- com.hotpot.ioc.context
    - IOC的核心实现，主要分为三步：<BR>
    1.扫描<BR>2.实例化<BR>3.依赖注入
### 待改进
主要是在扫描这一块有点问题，目前必须在`IocContext`类中写死`basePackage`，
还没想到怎么做成spring boot那样自动扫描
