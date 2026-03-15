package com.smj.workhub.security.principal;

import com.smj.workhub.user.entity.User;
import com.smj.workhub.user.repository.UserRepository;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads authenticated user details for Spring Security.
 * Spring Security will call this service during authentication.
 */
@Service
public class UserPrincipalService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserPrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Spring Security uses this method to load a user by username (email).
     */
    @Override
    public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email)
                );

        return new UserPrincipal(user);
    }
}
