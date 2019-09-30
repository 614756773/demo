#### zookeeper分布式锁
- 原理：利用zookeeper的有序临时节点来获取锁
    - `有序`的作用：能够看当前进程获得的节点是否是最小的，<BR>
    如果是就说明获得了锁
    - `临时`的作用：能够防止因为进程突然挂掉而导致锁未释放
- 代码逻辑
    - 首先创建公共的父节点
    - 调用ZookeeperLock#lock去获取锁
        - 在父节点下创建一个有序的临时节点
        - 如果是最小的，则获得锁，方法返回
        - 如果不是则调用Zookeeper#getChildren对父节点进行监听<BR>
        当监听事件触发时去判断当前节点是否是最小节点，如果不是则继续监听，<BR>
        直到是最小的，获取锁，方法返回

#### mysql可重入分布式锁
- 原理：当我们要锁住某个方法或资源时，我们就在该表中增加一条记录，想要释放锁的时候就删除这条记录。
    - 因为实现了可重入锁，所以有一个thread_name字段，<BR>
    当发现表里面已经有记录，并且thread_name和当前进程的相同，<BR>
    那么就获取到了可重入锁，并且要将count加1，<BR>
    释放锁时要知道count为0了才将数据库表的记录删除
- 建表
    ```sql
    CREATE TABLE `method_lock` (
      `id` int(11) NOT NULL AUTO_INCREMENT,
      `tag` varchar(255) NOT NULL,
      `thread_name` varchar(255) DEFAULT NULL COMMENT '线程名称，实现可重入锁',
      `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`),
      UNIQUE KEY `UNIQUE_TAG` (`tag`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
    ```
#### redis分布式锁
- 原理：使用setnx命令`和mysql实现分布式锁非常相`新建key，如果成功了就说明获得了锁
- 实现步骤
    - 循环使用setnx设置key，直到成功，表示已获取到锁
        - 获取锁后使用expire命令设置过期时间`防止进程挂掉导致死锁`
        - 启动一个辅助线程去定时对过期时间进行续约`防止业务还没处理完，锁就被释放了`
    - 释放锁
        - 使用lua脚本删除key<BR>`整个操作具有原子性`<BR> `查询key看value是否为该进程自己设置的，如果是就执行删除`
        - 关闭辅助线程