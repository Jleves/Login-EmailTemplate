package com.Login.Email.Service.Impl;


import com.Login.Email.Exception.JWT.InvalidTokenException;
import com.Login.Email.Model.Auth.PasswordResetToken;
import com.Login.Email.Repository.Email.PasswordResetTokenRepository;
import com.Login.Email.Repository.UserRepository;
import com.Login.Email.Security.PasswordEncoder;
import com.Login.Email.Service.Email.EmailService;
import com.Login.Email.Service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = Logger.getLogger(PasswordResetServiceImpl.class);

    @Override
    public void createPasswordResetToken(String email) {
        var user = userRepository.findByEmailIgnoreCase(email);

        logger.info("[SUCCESS] Usuario encontrado:  "+ user.getEmail());
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                null,
                token,
                user,
                LocalDateTime.now().plusMinutes(30),
                false
        );
        tokenRepository.save(resetToken);
        logger.info("[SUCCESS] Token guardado al usuario: "+ user);

        emailService.sendPasswordResetEmail(user.getUsername(),user.getEmail(),token);
    }

    @Override
    public boolean validateToken(String token) {


        var resetToken = tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && t.getExpiration().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new InvalidTokenException("Token inválido o expirado"));
        logger.info("[SUCCESS] token valido");

        return true;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        var resetToken = tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && t.getExpiration().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new InvalidTokenException("Token inválido o expirado"));

        var user = resetToken.getUser();
        user.setPassword(passwordEncoder.bCryptPasswordEncoder().encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        logger.info("[SUCCESS] Usuario {} restableció su contraseña exitosamente :  " + user.getEmail());
    }
}