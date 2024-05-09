package com.beyondrest.SegreteriaVirtualeREST.security;

import java.io.IOException;
import java.util.Optional;

import com.beyondrest.SegreteriaVirtualeREST.ErrorResponse;
import com.beyondrest.SegreteriaVirtualeREST.pianodistudi.PianoDiStudiController;
import com.beyondrest.SegreteriaVirtualeREST.studente.Studente;
import com.beyondrest.SegreteriaVirtualeREST.studente.StudenteService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final StudenteService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try{
            String authHeader = request.getHeader("Authorization");
                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            String token = authHeader.substring(7);
                            String userName = jwtService.extractUsername(token);
                            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                                UserDetails user = userService.loadUserByUsername(userName);
                                if (jwtService.validateToken(token, user)) {
                                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                            user, null, user.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Expired JWT token: " + e.getMessage());
            response.getWriter().flush();
        }
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleExpiredJwtException(ExpiredJwtException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED, "Expired JWT token: " + ex.getMessage());
    }
}
