package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp()
    {
        System.out.println("Running Setup");
        cartController = new CartController();
        TestUtils.injectObjects(cartController,"userRepository", userRepository);
        TestUtils.injectObjects(cartController,"cartRepository", cartRepository);
        TestUtils.injectObjects(cartController,"itemRepository", itemRepository);
        when(userRepository.findByUsername("tester")).thenReturn(sampleUser());

        when(itemRepository.findById(sampleUser().getCart().getItems().get(0).getId())).thenReturn(
                Optional.of(sampleUser().getCart().getItems().get(0))
        );
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
    public void testAddToCart()
    {
        ModifyCartRequest modifyCartRequest  = new ModifyCartRequest();
        modifyCartRequest.setUsername(sampleUser().getUsername());
        modifyCartRequest.setItemId(sampleUser().getCart().getItems().get(0).getId());
        modifyCartRequest.setQuantity(2);
        ResponseEntity<Cart> cart = this.cartController.addTocart(modifyCartRequest);
        assertEquals(200, cart.getStatusCodeValue());
        BigDecimal totalItemsOfThree =
                sampleUser().getCart().getTotal().multiply(BigDecimal.valueOf(3));
        assertEquals(totalItemsOfThree, cart.getBody().getTotal());
    }

    @Test
    public void testRemoveFromCard()
    {

        ModifyCartRequest modifyCartRequest  = new ModifyCartRequest();
        modifyCartRequest.setUsername(sampleUser().getUsername());
        modifyCartRequest.setItemId(sampleUser().getCart().getItems().get(0).getId());
        ResponseEntity<Cart> cart = this.cartController.removeFromcart(modifyCartRequest);
        assertEquals(200, cart.getStatusCodeValue());

    }
}
