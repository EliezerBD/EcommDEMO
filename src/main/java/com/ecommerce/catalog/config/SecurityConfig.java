package com.ecommerce.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // ¡Nuevo Importante!
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // ¡Nuevo Importante!
import org.springframework.security.crypto.password.PasswordEncoder; // ¡Nuevo Importante!
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuración de seguridad de la aplicación.
 * Define reglas de autenticación y autorización para los endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Credenciales externalizadas desde properties
    @Value("${security.admin.username:admin}")
    private String adminUsername;

    @Value("${security.admin.password:pass}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilitar CORS
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Definición de las reglas de autorización (Permisos)
                .authorizeHttpRequests((auth) -> auth

                        // 1. Acceso público: Swagger y Documentación
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 2. Acceso público: Solo las peticiones GET (Lectura de catálogo)
                        .requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**").permitAll()

                        // 3. Acceso protegido: POST, PUT, DELETE requieren autenticación
                        // (Para fines de test, el rol 'ADMIN' que simulas será suficiente)
                        .requestMatchers(HttpMethod.POST, "/api/v1/products").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").authenticated()

                        // 4. Denegar todo lo demás
                        .anyRequest().authenticated())
                // 3. Configurar autenticación HTTP básica (la que usan los tests simples)
                .httpBasic(withDefaults());

        return http.build();
    }

    /**
     * Configuración CORS para permitir peticiones desde el frontend.
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.List.of("*")); // En prod, cambiar '*' por el dominio del frontend
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ===========================================
    // NUEVAS CONFIGURACIONES PARA TEST Y SEGURIDAD
    // ===========================================

    /**
     * Define el codificador de contraseñas. BCrypt es el estándar.
     * Es OBLIGATORIO en Spring Security 6.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define un usuario de prueba en memoria para los tests.
     * ¡Importante! La contraseña debe estar codificada.
     * Credenciales obtenidas desde application.properties o variables de entorno.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // La contraseña codificada obtenida desde properties
        String encodedPassword = passwordEncoder().encode(adminPassword);

        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(encodedPassword)
                .roles("ADMIN") // El rol que simularemos en el test
                .build();

        // En un proyecto real, esto sería reemplazado por un UserRepository
        return new InMemoryUserDetailsManager(admin);
    }
}