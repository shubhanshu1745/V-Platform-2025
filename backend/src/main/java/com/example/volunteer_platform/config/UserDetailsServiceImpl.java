package com.example.volunteer_platform.config;

import com.example.volunteer_platform.entity.Role;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("loadUserByUsername: Attempting to load user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        logger.debug("loadUserByUsername: User found: {}", user.getEmail());
        logger.debug("loadUserByUsername: User roles before refresh: {}", user.getRoles());

        // No need to refresh, roles are fetched eagerly
        // entityManager.refresh(user);

        logger.debug("loadUserByUsername: User roles AFTER refresh (or no refresh): {}", user.getRoles());

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            logger.warn("loadUserByUsername: User '{}' has NO roles associated in the database!", user.getEmail());
        }

        logger.debug("loadUserByUsername: Creating UserDetails for user: {}", user.getEmail());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                getAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        logger.debug("getAuthorities: Converting roles to GrantedAuthorities. Roles: {}", roles);
        if (roles == null || roles.isEmpty()) {
            logger.debug("getAuthorities: Roles set is EMPTY.");
            return List.of(); // Return empty list if roles is null or empty
        }
        return roles.stream()
                .map(role -> {
                    logger.debug("getAuthorities: Mapping role: {} to GrantedAuthority", role.getRoleName());
                    return new SimpleGrantedAuthority(role.getRoleName().toString());
                })
                .collect(Collectors.toList());
    }
}