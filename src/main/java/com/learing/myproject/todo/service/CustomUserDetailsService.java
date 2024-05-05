package com.learing.myproject.todo.service;

import com.learing.myproject.todo.entity.User;
import com.learing.myproject.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> authUser = userRepository.findByUsername(username.toLowerCase());
        if (authUser.isEmpty()) {
            throw new UsernameNotFoundException(username);
        } else {
            return  org.springframework.security.core.userdetails.User.builder()
                    .username(authUser.get().getUsername())
                    .password(authUser.get().getPassword())
                    .build();
        }
    }
}