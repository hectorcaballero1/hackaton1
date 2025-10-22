package org.example.hackaton01.Eventos;
import org.springframework.context.ApplicationEvent;
public class UserRegisterEvent extends ApplicationEvent {

    private final String username;
    private final String email;

    public UserRegisterEvent(Object source, String username, String email) {
        super(source);
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}