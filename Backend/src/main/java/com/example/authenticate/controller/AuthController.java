package com.example.authenticate.controller;

import org.springframework.http.HttpHeaders;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.authenticate.io.AuthRequest;
import com.example.authenticate.io.AuthResponse;
import com.example.authenticate.io.ResetPasswordRequest;
import com.example.authenticate.service.AppUserDetailsService;
import com.example.authenticate.service.ProfileService;
import com.example.authenticate.util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/v1.0")
@CrossOrigin(origins = {"https://authify-frontend-m3im.onrender.com", "http://localhost:5173"}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ProfileService profileService;
    
    AuthController(AppUserDetailsService appUserDetailsService) {
        this.appUserDetailsService = appUserDetailsService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            final String jwtToken = jwtUtil.generateToken(
                        appUserDetailsService.loadUserByUsername(request.getEmail())
            );

            boolean isProd = !request.getEmail().endsWith("@localhost"); // simple toggle for dev vs prod

            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite(isProd ? "None" : "Lax") // Lax for local dev, None for prod
                    .secure(isProd)                        // false in dev, true in prod
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(request.getEmail(), jwtToken));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", true, "message", "Email or password is incorrect"));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", true, "message", "Account is disabled"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", true, "message", "Authentication failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        boolean isProd = false; // toggle same as above

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite(isProd ? "None" : "Lax")
                .secure(isProd)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully!");
    }

    private Authentication authenticate(String email, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }
    
    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(email != null);
    }
    
    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email) {
        try {
            profileService.sendResetOtp(email);
        }catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            profileService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        }catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }    
    }
    
    @PostMapping("/send-otp")
    public void sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
             profileService.sendOtp(email);
        }catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @PostMapping("/verify-otp")
    public void verifyEmail(@RequestBody Map<String, Object> request,
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
            
        if(request.get("otp") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Missing details");
        }
        
        try {
            profileService.verifyOtp(email, request.get("otp").toString());
           }catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
           }
    }
}
