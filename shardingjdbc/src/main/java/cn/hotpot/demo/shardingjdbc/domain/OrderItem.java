package cn.hotpot.demo.shardingjdbc.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * @author qinzhu
 * @since 2021/2/9
 */
@Entity(name = "t_order_item")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long orderItemId;

    @Column(updatable = false)
    private Long orderId;

    private String name;
}
