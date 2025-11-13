package com.example.projetoRestSpringBoot.dto.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
public class TokenDTO implements Serializable {
    private String username;
    @JsonIgnore
    private String password;
    private boolean authenticated;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime created;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expiration;
    private String accessToken;
    private String refreshToken;

    public TokenDTO(String username, boolean authenticated, Date created, Date expiration, String accessToken, String refreshToken) {
        this.username = username;
        this.authenticated = authenticated;
        this.created = created.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.expiration = expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
