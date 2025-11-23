package com.example.filme.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers("/bully/**").permitAll()

                        // Permições - Authentication
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                        // Permições - Filmes
                        .requestMatchers(HttpMethod.POST, "/filme").hasRole("ADM")
                        .requestMatchers(HttpMethod.PUT, "/filme/{id}").hasRole("ADM")
                        .requestMatchers(HttpMethod.POST, "/filme/inserirDaAPI/{pagina}").hasRole("ADM")

                        // Permições - Avaliação
                        .requestMatchers(HttpMethod.DELETE, "/avaliacao/{id}").hasRole("ADM")

                        // Permições - Usuário
                        .requestMatchers(HttpMethod.GET, "/usuario/all").hasRole("ADM")
                        .requestMatchers(HttpMethod.POST, "/usuario/desativar/{id}").hasRole("ADM")

                        // Permições - Gênero
                        .requestMatchers(HttpMethod.POST, "/genero/add").hasRole("ADM")
                        .requestMatchers(HttpMethod.POST, "/genero/add/api").hasRole("ADM")
                        .requestMatchers(HttpMethod.PUT, "/genero").hasRole("ADM")

                        // Permições - FilmePessoa
                        .requestMatchers(HttpMethod.POST, "/filmePessoa").hasRole("ADM")

                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}