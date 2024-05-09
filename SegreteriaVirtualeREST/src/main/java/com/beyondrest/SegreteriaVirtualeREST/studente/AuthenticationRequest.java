package com.beyondrest.SegreteriaVirtualeREST.studente;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
