package com.Login.Email.Service.Auth;


import com.Login.Email.Dto.UserDTO;
import com.Login.Email.Exception.JWT.InvalidCredentialsException;
import com.Login.Email.Model.Auth.AuthResponse;
import com.Login.Email.Model.Auth.LoginRequest;
import com.Login.Email.Model.Auth.RegisterRequest;
import com.Login.Email.Model.Enum.UserRol;
import com.Login.Email.Model.User;
import com.Login.Email.Security.JWTUtil;
import com.Login.Email.Security.PasswordEncoder;
import com.Login.Email.Service.Impl.UserServiceImpl;
import com.Login.Email.Utils.AppProperties;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JWTUtil jwtUtil;  //Para generar el token
    private final PasswordEncoder passwordEncoder; //Encriptar el TOKEN
    private final UserServiceImpl userService; //Para buscar el usuario

    private RefreshTokenService refreshTokenService;
    private AppProperties appProperties;

    private static final Logger logger = Logger.getLogger(AuthService.class);
@Autowired
    public AuthService( JWTUtil jwtUtil, PasswordEncoder passwordEncoder, UserServiceImpl userService, RefreshTokenService refreshTokenService, AppProperties appProperties, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    this.userService = userService;
    this.refreshTokenService = refreshTokenService;
        this.appProperties = appProperties;
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    private final AuthenticationManager authenticationManager; // Para que se autentique
    public AuthResponse login(LoginRequest loginRequest) {

        try {



            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
            logger.info("Usuario autenticado con exito");

            // ya autenticado por Spring Security.
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();



            User user = userService.findUsername(loginRequest.getUsername())
                    .orElseThrow();

            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setRol(user.getRol().getAuthority());

            String token = jwtUtil.generateToken(userDetails);



            String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername(), user.getId()).getToken();

            logger.info("Creacion de token y refresh token ok");

            Long expiration= appProperties.getSecurity().getJwt().getAccessExpirationMinutes()*60;



            return AuthResponse.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(expiration)
                    .user(userDTO)
                    .build();


        }catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Usuario o contraseña incorrectos");
        }

    }

    public AuthResponse register (RegisterRequest registerRequest){
        if (userService.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }


        User user = User.builder()
                .password(passwordEncoder.bCryptPasswordEncoder().encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .rol(UserRol.USER) //Se puede cambiar a invitado hasta que confirme el email
                .isEnabled(false).build();

        User newUser = userService.saveUser(user);

        logger.info("Usuario registrado y guardado en base de datos");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(newUser.getId());
        userDTO.setUsername(newUser.getUsername());
        userDTO.setRol(newUser.getRol().getAuthority());

        Long expiration= appProperties.getSecurity().getJwt().getAccessExpirationMinutes()*60;

        String token = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user.getUsername(), user.getId()).getToken();
        logger.info("Generacion de token y refresh token ok");
        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(token)
                .refreshToken(refreshToken)
                .expiresIn(expiration)
                .user(userDTO)
                .build();
    }

}
/*

 System.out.println("Auth Service: --->  username: " + loginRequest.getUsername() +"  Pin:  "+ loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow();

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRol(user.getRol().getAuthority());

        String token = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .expiresIn(3600)
                .user(userDTO)
                .build();

 */