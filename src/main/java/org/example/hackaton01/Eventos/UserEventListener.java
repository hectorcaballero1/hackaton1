package org.example.hackaton01.Eventos;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    @EventListener
    public void handleUserRegisterEvent(UserRegisterEvent event) {
        System.out.println("Nuevo usuario registrado: " + event.getUsername() + ", email: " + event.getEmail());

    }
}
