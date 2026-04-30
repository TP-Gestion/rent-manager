package ar.com.aeb.alquileres.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Permite cualquier origen (usando patrones para soportar credenciales)
                .allowedOriginPatterns("*")
                // Métodos permitidos
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                // Permite todos los headers
                .allowedHeaders("*")
                // Necesario si el frontend manda cookies o tokens de autorización
                .allowCredentials(true)
                // Cachea la respuesta del OPTIONS (preflight) por 1 hora
                .maxAge(3600);
    }
}
