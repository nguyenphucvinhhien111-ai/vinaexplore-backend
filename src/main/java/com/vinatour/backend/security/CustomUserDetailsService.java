package com.vinatour.backend.security;

import com.vinatour.backend.entity.User;
import com.vinatour.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;

        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + identifier));
        } else {
            user = userRepository.findByUsername(identifier)
                    .orElseThrow(
                            () -> new UsernameNotFoundException("Không tìm thấy user với username: " + identifier));
        }

        String password = user.getPassword() != null ? user.getPassword() : "";

        return new org.springframework.security.core.userdetails.User(
                identifier,
                password,
                user.getActive() != null ? user.getActive() : true, 
                true, 
                true, 
                true, 
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())));
    }
}