package com.beyondrest.SegreteriaVirtualegRPC.studente;

import org.springframework.security.core.GrantedAuthority;

public enum Ruolo implements GrantedAuthority {
    ROLE_STUDENTE("STUDENTE"),
    ROLE_DOCENTE("DOCENTE"),
    ROLE_ADMIN("ADMIN");

    private final String value;

    Ruolo(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
