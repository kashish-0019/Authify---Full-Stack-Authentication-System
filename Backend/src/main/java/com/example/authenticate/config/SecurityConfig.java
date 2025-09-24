package com.example.authenticate.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.example.authenticate.filter.JwtRequestFilter;
import com.example.authenticate.service.AppUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private AppUserDetailsService appUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	@Autowired
	private CustomAuthEntryPoint customAuthEntryPoint;
	
    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()) // Enable CORS
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF (since we are building REST APIs)
			.authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/register", "/send-reset-otp", "/reset-password", "/logout", "/is-authenticated")
            .permitAll() // these endpoints can be accessed without authentication
            .anyRequest().authenticated() // everything else needs authentication
             )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no sessions (stateless JWT)
            .logout(AbstractHttpConfigurer::disable) // disable default logout (we handle manually)
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex ->ex.authenticationEntryPoint(customAuthEntryPoint));
        return http.build();
    }

    // Password encoder (used for hashing user passwords)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS filter
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }


    // CORS configuration
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "https://authify-frontend-m3im.onrender.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // allowed HTTP methods
        config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // headers allowed in request
        config.setAllowCredentials(true); // allow cookies/authorization headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // apply config to all endpoints
        return source;
    }
    
    @Bean
    public AuthenticationManager authenticationManager() {
    	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    	authenticationProvider.setUserDetailsService(appUserDetailsService);
    	authenticationProvider.setPasswordEncoder(passwordEncoder());
    	return new ProviderManager(authenticationProvider);
    }
}
