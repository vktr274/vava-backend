package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.models.Order;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.repositories.OrderRepository;

import javax.persistence.NoResultException;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUser(User user) {
        var orders = orderRepository.findAllByUser(user).orElse(null);
        if (orders == null) {
            throw new NoResultException("no orders for " + user.getUsername());
        }
        return orders;
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
