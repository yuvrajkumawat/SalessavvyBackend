package com.kodnest.student.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.security.Key;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kodnest.student.entity.JWTToken;
import com.kodnest.student.entity.User;
import com.kodnest.student.repository.JWTTokenRepository;
import com.kodnest.student.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

    private final Key SIGNING_KEY;
    private final UserRepository userRepository;
    private final JWTTokenRepository jwtTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository,
                       JWTTokenRepository jwtTokenRepository,
                       @Value("${jwt.secret}") String jwtSecret) {

        this.userRepository = userRepository;
        this.jwtTokenRepository = jwtTokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();

        // Secret length validation (HS512 needs 64 bytes)
        if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
            throw new IllegalArgumentException(
                "JWT_SECRET in application.properties must be at least 64 bytes long for HS512."
            );
        }

        this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ Authenticate User
    public User authenticate(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }

    // ✅ Generate Token
    public String generateToken(User user) {

        String token;
        LocalDateTime now = LocalDateTime.now();

        JWTToken existingToken = jwtTokenRepository.findByUserId(user.getUserId());

        if (existingToken != null && now.isBefore(existingToken.getExpiresAt())) {
            token = existingToken.getToken();
        } else {
            token = generateNewToken(user);

            if (existingToken != null) {
                jwtTokenRepository.delete(existingToken);
            }
        }

        saveToken(user, token);
        return token;
    }

    // ✅ Create New JWT
    private String generateNewToken(User user) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    // ✅ Save Token in DB
    public void saveToken(User user, String token) {

        JWTToken jwtToken = new JWTToken(
                user,
                token,
                LocalDateTime.now().plusHours(1)
        );

        jwtTokenRepository.save(jwtToken);
    }
    
 // ✅ Logout Token in DB
    public void logout(User user) {
        jwtTokenRepository.deleteByUserId(user.getUserId());
    }

    public boolean validateToken(String token) {

        try {
            System.out.println("VALIDATING TOKEN...");

            // 1️⃣ Parse & verify signature
                    Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 2️⃣ Check token exists in DB
            Optional<JWTToken> jwtTokenOptional = jwtTokenRepository.findByToken(token);

            if (jwtTokenOptional.isEmpty()) {
                System.out.println("Token not found in database");
                return false;
            }

            JWTToken jwtToken = jwtTokenOptional.get();

            System.out.println("Token Expiry (DB): " + jwtToken.getExpiresAt());
            System.out.println("Current Time: " + LocalDateTime.now());

            // 3️⃣ Check expiry from DB
            if (jwtToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                System.out.println("Token expired");
                return false;
            }

            return true;

        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {

            return Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
             
    }

	
}