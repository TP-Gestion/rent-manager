package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.auth.LoginRequest;
import ar.com.aeb.alquileres.dto.auth.LoginResponse;
import ar.com.aeb.alquileres.dto.auth.RegisterRequest;
import ar.com.aeb.alquileres.model.User;
import ar.com.aeb.alquileres.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // ¡¡¡ADVERTENCIA: Comparación de contraseña en texto plano!!!
        // Esto es extremadamente inseguro. Solo para prototipado.
        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return new LoginResponse(user);
    }

    public LoginResponse register(RegisterRequest request) {
        // 1. Validar que el email no esté en uso
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        // 2. Crear el nuevo usuario (la contraseña se guarda en texto plano)
        User newUser = new User(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        // 3. Guardar el usuario en la base de datos
        User savedUser = userRepository.save(newUser);

        // 4. Devolver una respuesta (similar al login, para "auto-loguear" al usuario)
        return new LoginResponse(savedUser);
    }
}