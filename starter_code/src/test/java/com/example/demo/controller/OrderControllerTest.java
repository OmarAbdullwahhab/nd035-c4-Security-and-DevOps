package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.apache.catalina.LifecycleState;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {


    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp()
    {
        System.out.println("Running Setup");
        orderController = new OrderController();
        TestUtils.injectObjects(orderController,"userRepository", userRepository);
        TestUtils.injectObjects(orderController,"orderRepository", orderRepository);

        when(userRepository.findByUsername("tester")).thenReturn(
                sampleUser()
        );
        when(orderRepository.findByUser(sampleUser())).thenReturn(
                Arrays.asList(
                        sampleOrder()
                )
        );

    }
    private UserOrder sampleOrder()
    {
        UserOrder order = new UserOrder();
        order.setTotal(sampleUser().getCart().getTotal());
        order.setUser(sampleUser());
        order.setItems(sampleUser().getCart().getItems());
        order.setId(1l);
        return order;
    }

    private User sampleUser()
    {
        User user = new User();
        user.setUsername("tester");
        user.setPassword("Tester123**");
        user.setId(1);
        Cart cart = new Cart();
        cart.setId(1l);
        cart.setUser(user);
        Item item = new Item();
        item.setId(1l);
        item.setDescription("Sample Item");
        item.setName("Sample");
        item.setPrice(BigDecimal.valueOf(1500.50));
        cart.addItem(item);
        cart.setTotal(BigDecimal.valueOf(1500.50));
        user.setCart(cart);
        return user;
    }

    @Test
    public void testSubmitOrder()
    {
        ResponseEntity<UserOrder> created = this.orderController.submit(this.sampleUser().getUsername());
        assertEquals(200,created.getStatusCodeValue());
        assertEquals(BigDecimal.valueOf(1500.50),created.getBody().getTotal());
    }

    @Test
    public void testGetOrdersForUser()
    {
        ResponseEntity<List<UserOrder>> found = this.orderController.getOrdersForUser(sampleUser().getUsername());
        assertEquals(200, found.getStatusCodeValue());

    }

}
