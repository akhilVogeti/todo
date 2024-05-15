package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        testUser = User.builder()
                .id("testUser")
                .username("testUser")
                .password(passwordEncoder.encode("password"))
                .build();
    }


    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testLoadByUsername_ReturnUserDetails() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.ofNullable(testUser));

        UserDetails fetchedUserDetails = customUserDetailsService.loadUserByUsername("testUser");

        Assert.assertEquals("testUser",fetchedUserDetails.getUsername());
        Assert.assertEquals("encodedPassword",fetchedUserDetails.getPassword());
    }

    @Test
    public void testLoadByUsername_ReturnException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Assert.assertThrows(UsernameNotFoundException.class, ()-> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });
    }
}
