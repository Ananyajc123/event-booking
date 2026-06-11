package com.eventbooking.service;
import com.eventbooking.config.JwtUtil;
import com.eventbooking.dto.*;
import com.eventbooking.model.User;
import com.eventbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
@Service
public class AuthService implements UserDetailsService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) throw new RuntimeException("Email already registered");
        User user = User.builder().name(req.getName()).email(req.getEmail()).password(passwordEncoder.encode(req.getPassword())).build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(loadUserByUsername(req.getEmail()));
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole());
    }
    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtUtil.generateToken(loadUserByUsername(req.getEmail()));
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole());
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
