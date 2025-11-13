package com.example.projetoRestSpringBoot.dto.security;

import lombok.Data;
import java.io.Serializable;

@Data

public class AccountCredentialsDTO implements Serializable {
    private String username;
    private String fullname;
    private String password;

    public AccountCredentialsDTO() {
    }

    public AccountCredentialsDTO(String username, String fullname, String password) {
        this.username = username;
        this.fullname = fullname;
        this.password = password;
    }
}
