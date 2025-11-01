package org.example.hackaton01.user.domain;

import lombok.RequiredArgsConstructor;
import org.example.hackaton01.user.dto.UserResponse;
import org.example.hackaton01.user.infrastructure.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
     /*userDatailService es una interfaz propia de spring Security que tiene un unico metodo obligatorio
     * public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
     * su objetivo es permitirle a spring security cargar los datos del usuario(desde la base de datos )  cuando alguien intenta iniciar sesion */


    /* para obtener usuarios- get/users /solo CENTRAL
    *  obtenemos lista de usuarios solo para los de rol CENTRAL  */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }


    /* para el get/user{id}(solo CENTRAL )
    * mostrmaos detalles del usuario al buscar por el id */

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + id));
        return modelMapper.map(user, UserResponse.class);
    }

    /* para el delete user/{id} solo los central
    * eliminamos usuario por el id */

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException("usuario no enconrado con el id " + id);
        }
        userRepository.deleteById(id);
    }
    /*metodo para autenticacion interna , cuando el auth service necesite la entidad completa para verificar la contraseña*/
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("usuario no encontrado " + username));
    }
}
