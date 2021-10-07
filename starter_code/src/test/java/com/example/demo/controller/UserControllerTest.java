package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.LoginRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.validation.constraints.AssertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UserControllerTest {


  //  @Autowired
   // private WebTestClient webTestClient;

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    private Long userId;
    private String userName;
    private String password;

    @Before
    public void setUp()
    {
        System.out.println("Running Setup");
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository", userRepository);
        TestUtils.injectObjects(userController,"cartRepository", cartRepository);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder", bCryptPasswordEncoder);
        when(userRepository.findByUsername("tester")).thenReturn(sampleUser());
        when(userRepository.findById(1l)).thenReturn(Optional.of(sampleUser()));
        when(userRepository.save(sampleUser())).thenReturn(sampleUser());
        when(cartRepository.findByUser(sampleUser())).thenReturn(sampleUser().getCart());
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
        cart.setTotal(BigDecimal.valueOf(1500.50));
        user.setCart(cart);
        return user;
    }

    @Test
    public void testCreateUser()
    {
        this.userName = sampleUser().getUsername();
        this.password = sampleUser().getPassword();
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(this.userName);
        request.setPassword(this.password);
        request.setConfirmPassword(this.password);
        ResponseEntity<User> found = this.userController.findByUserName(this.userName);
        if(found.getStatusCodeValue() == 200)
        {

            User u = found.getBody();
            this.userId = sampleUser().getId();
            System.out.println("User found with id -> " + this.userId);
        }
        else {
            ResponseEntity<User> created = userController.createUser(request);
            User u = created.getBody();
            this.userId = u.getId();
            System.out.println("User created with id -> " + this.userId);
            assertEquals(u.getUsername(),this.userName);
        }
    }
    @Test
    public void testCreateUserBadRequest()
    {
        this.userName = "baduser";
        this.password = "1";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(this.userName);
        request.setPassword(this.password);
        request.setConfirmPassword(this.password);
        ResponseEntity<User> found = this.userController.createUser(request);
        assertFalse(found.getStatusCodeValue() == 200);

    }
    @Test
    public void testFindById()
    {
        ResponseEntity<User> found = this.userController.findById(1l);
        assertNotNull(found);
        assertEquals(sampleUser().getId(),found.getBody().getId());
    }

    @Test
    public void testFindByUsername()
    {
        ResponseEntity<User> found = this.userController.findByUserName("tester");
        assertNotNull(found);
        assertEquals(sampleUser().getId(), found.getBody().getId());

    }

}
