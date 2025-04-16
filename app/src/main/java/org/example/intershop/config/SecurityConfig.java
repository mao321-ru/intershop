package org.example.intershop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain( ServerHttpSecurity http) {
        return http
            .authorizeExchange( exchanges -> exchanges
                .pathMatchers("/", "/products/**").permitAll()
                .pathMatchers("/config").hasRole( "ADMIN")
                .anyExchange().authenticated()
            )
            .formLogin( Customizer.withDefaults())
            .logout( logout -> logout
                .logoutUrl( "/logout")
                .logoutSuccessHandler((exchange, authentication) ->
                    exchange.getExchange().getSession()
                        .flatMap( WebSession::invalidate) // удаляем сессию
                        .then( Mono.fromRunnable(() -> {
                            var resp = exchange.getExchange().getResponse();
                            // переход на витрину после выхода
                            resp.setStatusCode( HttpStatus.FOUND);
                            resp.getHeaders().setLocation( URI.create( "/"));
                        }))
                )
            )
            .build();
    }
}
