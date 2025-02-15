package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository repository;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    public void increaseFailsCounter(String username){
        User user = repository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        user.setFailsCounter(user.getFailsCounter()+1);
        if (user.getFailsCounter() >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocker(false);
        }
        System.out.println(user.isAccountNonLocked());
        System.out.println(user.getFailsCounter());
        repository.save(user);
    }
    public void resetFailsCounter(String username){
        User user = repository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        user.setFailsCounter(0);
    }
}
