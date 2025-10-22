package org.example.hackaton01.Eventos;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
@Component
public class UserEventPublisher {

    private final ApplicationEventPublisher publisher;

    public UserEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishUserRegisterEvent(String username, String email) {
        UserRegisterEvent event = new UserRegisterEvent(this, username, email);
        publisher.publishEvent(event);
    }
}
