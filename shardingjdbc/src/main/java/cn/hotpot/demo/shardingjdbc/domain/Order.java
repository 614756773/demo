package cn.hotpot.demo.shardingjdbc.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author qinzhu
 * @since 2021/2/9
 */
@Entity(name = "t_order")
@Data
@Accessors(chain = true)
public class Order {
    /**
     * orderId和userId作为了Sharding key，不允许更新。
     * 否则在更新时会抛出 ShardingSphereException: Can not update sharding key, logic table: [***]异常
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long orderId;

    @Column(updatable = false)
    private Long userId;

    private String name;
}
