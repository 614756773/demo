package cn.hotpot.demo.shardingjdbc.controller;

import cn.hotpot.demo.shardingjdbc.domain.Order;
import cn.hotpot.demo.shardingjdbc.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
}
