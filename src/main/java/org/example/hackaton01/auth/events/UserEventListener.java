package org.example.hackaton01.auth.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hackaton01.report.service.EmailService;
import org.example.hackaton01.user.domain.User;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleUserRegistration(UserRegisteredEvent event) {
        try {
            log.info("👋 Enviando email de bienvenida a: {}", event.getUser().getEmail());


            sendWelcomeEmail(event.getUser(), event.getPlainPassword());

        } catch (Exception e) {
            log.error(" Error enviando email de bienvenida: {}", e.getMessage());

        }
    }

    private void sendWelcomeEmail(User user, String plainPassword) {
        String subject = " ¡Bienvenido a Oreo Insights!";
        String text = String.format(
                "¡Hola %s! \n\n" +
                        "Tu cuenta en Oreo Insights ha sido creada exitosamente.\n\n" +
                        ".  Detalles de tu cuenta:\n" +
                        "   • Usuario: %s\n" +
                        "   • Email: %s\n" +
                        "   • Rol: %s\n" +
                        "   • Sucursal: %s\n\n" +
                        " Contraseña temporal: %s\n\n" +
                        "Por seguridad, te recomendamos cambiar tu contraseña después del primer inicio de sesión.\n\n" +
                        "¡Estamos emocionados de tenerte en nuestro equipo!\n\n" +
                        "Saludos,\nEquipo Oreo Insights",
                user.getUsername(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBranch() != null ? user.getBranch() : "Todas (CENTRAL)",
                plainPassword
        );

        emailService.sendWelcomeEmail(user.getEmail(), subject, text);
    }
}