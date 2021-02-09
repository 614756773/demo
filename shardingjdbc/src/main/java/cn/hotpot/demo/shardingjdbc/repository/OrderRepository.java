package cn.hotpot.demo.shardingjdbc.repository;

import cn.hotpot.demo.shardingjdbc.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * @author qinzhu
 * @since 2021/2/9
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * 更具orderId查询
     */
    @Query("select o from t_order o where o.orderId = :orderId")
    Order findByOrderId(@Param("orderId") Long orderId);
}
