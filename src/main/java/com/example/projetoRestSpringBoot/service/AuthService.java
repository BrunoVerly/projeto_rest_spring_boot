package com.example.projetoRestSpringBoot.service;


import com.example.projetoRestSpringBoot.dto.security.AccountCredentialsDTO;
import com.example.projetoRestSpringBoot.dto.security.TokenDTO;
import com.example.projetoRestSpringBoot.exception.RequiredObjectIsNullException;
import com.example.projetoRestSpringBoot.model.User;
import com.example.projetoRestSpringBoot.repository.UserRepository;
import com.example.projetoRestSpringBoot.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                       credentials.getUsername(),
                        credentials.getPassword()
                )
        );

        var user = repository.findByUsername(credentials.getUsername());
        if(user == null){
            throw new UsernameNotFoundException("Usuário "+ credentials.getUsername() +" nao localizado");
        }
        var tokenResponse = tokenProvider.createAcessToken(credentials.getUsername(), user.getRoles());
        return ResponseEntity.ok(tokenResponse);
    }

    public ResponseEntity<TokenDTO> refreshToken(String username, String refreshToken){
        var user = repository.findByUsername(username);
        TokenDTO token;
        if(user != null) {
            token = tokenProvider.refreshToken(refreshToken);
        }else {
            throw new UsernameNotFoundException("Usuário " + username + " nao localizado");
        }
        return ResponseEntity.ok(token);
    }

    private String generatedHashedPassword(String password){
            PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8, 185000,
                    Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
            Map<String, PasswordEncoder> encoders = new HashMap<>();
            encoders.put("pbkdf2", pbkdf2Encoder);
            DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);

            passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get("pbkdf2"));
            return passwordEncoder.encode(password);
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO user){
        if(user == null){
            throw new RequiredObjectIsNullException();
        }
        var entity = new User();
        entity.setUsername(user.getUsername());
        entity.setFullname(user.getFullname());
        entity.setPassword(generatedHashedPassword(user.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);

        var dto =  repository.save(entity);
        return new AccountCredentialsDTO(dto.getUsername(), dto.getFullname(), dto.getPassword());
    }

}

