package sk.vava.zalospevaci.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.vava.zalospevaci.exceptions.NotFoundException;
import sk.vava.zalospevaci.models.Order;
import sk.vava.zalospevaci.models.User;
import sk.vava.zalospevaci.repositories.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Page<Order> getByUser(User user, Pageable pageable)
            throws NotFoundException
    {
        Page<Order> orders;
        if (user.getRole().equals("admin")) {
            orders = orderRepository.findAll(pageable);
        } else {
            orders = orderRepository.findAllByUser(user, pageable).orElse(null);
        }
        if (orders == null) {
            throw new NotFoundException(
                    "orders of user '" + user.getUsername() + "' are not found"
            );
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
