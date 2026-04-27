package ar.com.aeb.alquileres.service;

import ar.com.aeb.alquileres.dto.login.LoginRequest;
import ar.com.aeb.alquileres.dto.login.LoginResponse;
import ar.com.aeb.alquileres.dto.login.RegisterRequest;
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
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return new LoginResponse(user);
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        User newUser = new User(
                request.getName(), request.getEmail(), request.getPassword()
        );

        User savedUser = userRepository.save(newUser);

        return new LoginResponse(savedUser);
    }
}
