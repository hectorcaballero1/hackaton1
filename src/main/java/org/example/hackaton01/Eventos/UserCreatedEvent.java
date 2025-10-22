package org.example.hackaton01.Eventos;

public class UserCreatedEvent {

    private final String userId;
    private final String username;

    public UserCreatedEvent(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
