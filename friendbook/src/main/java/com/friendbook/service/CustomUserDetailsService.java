package com.friendbook.service;

import java.util.List;

import com.friendbook.entity.User;
import com.friendbook.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepo;
    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        User dbUser =  userRepo.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("Username not found"));

        GrantedAuthority authority = new SimpleGrantedAuthority("USER");

        return new org.springframework.security.core.userdetails
                .User(dbUser.getEmail(), dbUser.getPassword(), List.of(authority));
    }

}