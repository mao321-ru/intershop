package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository repo;

    @Override
    public Mono<UserDetails> findByUsername( String username) {
        return repo.findByLogin( username)
                .map( u -> org.springframework.security.core.userdetails.User
                    .withUsername( u.getLogin())
                    .password( u.getPasswordHash())
                    .roles( u.getAdminFlag() == 1 ? "ADMIN" : "USER")
                    .build()
                );
    }

}
