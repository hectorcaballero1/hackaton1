package org.example.hackaton01.auth.events;

import lombok.Getter;
import org.example.hackaton01.user.domain.User;
import org.springframework.context.ApplicationEvent;
@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final User user;
    private final String plainPassword;

    public UserRegisteredEvent(Object source, User user, String plainPassword) {
        super(source);
        this.user = user;
        this.plainPassword = plainPassword;
    }
}