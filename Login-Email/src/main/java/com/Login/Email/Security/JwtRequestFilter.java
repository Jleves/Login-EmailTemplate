package com.Login.Email.Security;


import com.Login.Email.Service.Email.Impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);//Sacamos la autenticacion del encabezado
        String username= null;
        String jwt = null;
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) { //Si la autenticacion Header comienza con Bearer...
                jwt = authorizationHeader.substring(7);//Apartir del orden 7 hasta el final es el token
                username = jwtUtil.extractUserName(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userService.loadUserByUsername(username);
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }catch (io.jsonwebtoken.ExpiredJwtException ex) {
            throw new com.Login.Email.Exception.JWT.CustomExpiredJwtException("Token expirado", ex);
        } catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.SignatureException ex) {
            throw new com.Login.Email.Exception.JWT.CustomInvalidJwtException("Token inválido o manipulado", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado al procesar el JWT", ex);
        }




        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
