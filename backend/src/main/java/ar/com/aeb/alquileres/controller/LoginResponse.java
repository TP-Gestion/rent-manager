package ar.com.aeb.alquileres.dto.auth;

import ar.com.aeb.alquileres.model.User;

public class LoginResponse {
    private Long id;
    private String name;
    private String email;
    private String dummyToken;

    public LoginResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        // Generamos un "token" falso para simular el flujo
        this.dummyToken = "dummy-token-for-user-" + user.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDummyToken() {
        return dummyToken;
    }

    public void setDummyToken(String dummyToken) {
        this.dummyToken = dummyToken;
    }
}