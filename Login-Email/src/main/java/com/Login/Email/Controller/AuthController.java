package com.Login.Email.Controller;


import com.Login.Email.Exception.ResourceNotFoundException;
import com.Login.Email.Model.Auth.*;
import com.Login.Email.Model.User;
import com.Login.Email.Security.JWTUtil;
import com.Login.Email.Service.Auth.AuthService;
import com.Login.Email.Service.Auth.RefreshTokenService;

import com.Login.Email.Service.Email.Impl.UserServiceImpl;
import com.Login.Email.Service.Email.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserServiceImpl userService;
    private final JWTUtil jwtUtil;

    private static final Logger logger = Logger.getLogger(AuthController.class);

    private RefreshTokenService refreshTokenService;
@Autowired
    public AuthController(AuthService authService, UserServiceImpl userService, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){


        logger.info("Usuario intentado acceder: " + loginRequest.getUsername());

        return ResponseEntity.ok(authService.login(loginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse>register(@Valid @RequestBody RegisterRequest resgisterRequest){
        logger.info("Nuevo registro: "+ resgisterRequest);
    return ResponseEntity.ok(authService.register(resgisterRequest)
    );
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestToken = request.getRefreshToken();


        return refreshTokenService.findByToken(requestToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    // Eliminamos o invalidamos el token usado
                    refreshTokenService.delete(refreshToken.getUser().getId());

                    // Creamos nuevos tokens
                    User user = refreshToken.getUser();
                    UserDetails userDetails = user;

                    String newAccessToken = jwtUtil.generateToken(userDetails);
                    String newRefreshToken = refreshTokenService.createRefreshToken(user.getUsername(), user.getId()).getToken();

                    return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, newRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));
    }

    @GetMapping("/check")
    public ModelAndView confirmUserAccount(@RequestParam("token") String token) throws ResourceNotFoundException {

        Boolean isSuccess = userService.verifyToken(token);
        boolean  verificar = true;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("Confirmacion"); // Nombre de la plantilla HTML
        modelAndView.addObject("verificar", verificar); // Pasar datos a la plantilla si es necesario
        modelAndView.addObject("timeStamp", LocalDateTime.now().toString()); // Ejemplo de pasar datos a la plantilla

        return modelAndView;
    }


    @GetMapping("/invitado")
    public String invitado(){
    return "Hola soy invitado";
    }

    @GetMapping("/user")
    public String user(){
        return "Hola soy user";
    }

    @GetMapping("/administrador")
    public String administrador(){
        return "Hola soy administrador";
    }

    @GetMapping("/superadmin")
    public String superadmin(){
        return "Hola soy superadmin ";
    }


}


