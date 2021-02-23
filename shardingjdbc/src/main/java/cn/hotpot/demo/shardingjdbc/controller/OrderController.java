package cn.hotpot.demo.shardingjdbc.controller;

import cn.hotpot.demo.shardingjdbc.domain.Order;
import cn.hotpot.demo.shardingjdbc.repository.OrderRepository;
import cn.hotpot.demo.shardingjdbc.repository.XAOrderService;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author qinzhu
 * @since 2021/2/8
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final XAOrderService xaOrderService;

    @PostMapping
    public ResponseEntity<String> hello(Long userId, String name) {
        Order order = new Order()
                .setUserId(userId)
                .setName(name);
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<Order>> page(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Order> result = orderRepository.findAll(pageRequest);
        return ResponseEntity.ok(result);
    }

    @PutMapping
    public ResponseEntity<Void> modify(Long orderId, String name) {
        Order order = orderRepository.findByOrderId(orderId);
        order.setName(name);
        orderRepository.save(order);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/distribute-transaction/fail")
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
    public ResponseEntity<Void> distributeTransactionFail() {
        xaOrderService.insertFailed(10);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/distribute-transaction/success")
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
    public ResponseEntity<Void> distributeTransactionSuccess() {
        xaOrderService.insert(10);
        return ResponseEntity.ok().build();
    }
}
