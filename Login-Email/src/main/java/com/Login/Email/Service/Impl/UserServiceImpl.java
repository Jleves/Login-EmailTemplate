package com.Login.Email.Service.Impl;


import com.Login.Email.Model.Email.Confirmation;

import com.Login.Email.Model.User;
import com.Login.Email.Repository.Email.ConfirmationRepository;
import com.Login.Email.Repository.UserRepository;
import com.Login.Email.Service.Email.EmailService;
import com.Login.Email.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailServiceImple;

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        logger.info("Inicio de registro de usuario");
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exist");
        }
        user.setEnabled(false);
        userRepository.save(user);

        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);

        logger.info("Registro exitoso");
        //Envio de email
       // emailServiceImple.sendSimpleMailMessage(user.getName(), user.getEmail(),confirmation.getToken());
        //emailServiceImple.sendHtmlEmailWithEmbeddedFiles(user.getName(), user.getEmail(),confirmation.getToken());
        emailServiceImple.sendHtmlEmail(user.getUsername(), user.getEmail(),confirmation.getToken());
        //emailServiceImple.sendMimeMessageWithEmbeddedFiles(user.getName(), user.getEmail(),confirmation.getToken());
        //emailServiceImple.sendMimeMessageWithAttachments(user.getName(), user.getEmail(),confirmation.getToken());
        logger.info("Envio de email");
        return user;
    }

    @Override
    public Boolean verifyToken(String token) {
        logger.info("Inicio Confirmacion");
        Confirmation confirmation= confirmationRepository.findByToken(token);
        User user= userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);
        logger.info("Enabled User true");
        userRepository.save(user);
        confirmationRepository.delete(confirmation);

        logger.info("Verification email ok");
        return Boolean.TRUE;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Optional<User> buscarUsuario = findUsername(username);

        if(buscarUsuario.isPresent()){
            return buscarUsuario.get();

        }else throw new UsernameNotFoundException("No se encontro el usuario");

    }
    public Optional<User> findUsername(String username){return  userRepository.findByUsername(username);}


    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
