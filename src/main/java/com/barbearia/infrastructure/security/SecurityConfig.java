package com.barbearia.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails owner = User.withDefaultPasswordEncoder()
                .username("dono")
                .password("dono123")
                .roles("DONO")
                .build();

        UserDetails barber = User.withDefaultPasswordEncoder()
                .username("barbeiro")
                .password("barbeiro123")
                .roles("BARBEIRO")
                .build();

        UserDetails client = User.withDefaultPasswordEncoder()
                .username("cliente")
                .password("cliente123")
                .roles("CLIENTE")
                .build();

        return new InMemoryUserDetailsManager(owner, barber, client);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/owners/**").hasRole("DONO")
                        .requestMatchers("/api/barbers/**").hasRole("BARBEIRO")
                        .requestMatchers("/api/clients/**").hasRole("CLIENTE")
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
