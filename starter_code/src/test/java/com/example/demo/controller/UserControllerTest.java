package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
        this.createUser();
    }

    @Test
    public void createUser()
    {
        this.userName = "tester";
        this.password = "Tester123**";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(this.userName);
        request.setPassword(this.password);
        request.setConfirmPassword(this.password);
        ResponseEntity<User> found = this.userController.findByUserName(this.userName);
        if(found.getStatusCodeValue() == 200)
        {

            User u = found.getBody();
            this.userId = u.getId();
            System.out.println("User found with id -> " + this.userId);
        }
        else {
            ResponseEntity<User> created = userController.createUser(request);
            User u = created.getBody();
            this.userId = u.getId();
            assertEquals(u.getUsername(),this.userName);
        }
    }

}
