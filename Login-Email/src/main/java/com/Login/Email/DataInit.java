package com.Login.Email;



import com.Login.Email.Model.Enum.UserRol;
import com.Login.Email.Model.User;
import com.Login.Email.Repository.UserRepository;
import com.Login.Email.Security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInit implements CommandLineRunner {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public DataInit(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;
    }




    @Override
    public void run(String... args) throws Exception {


        User admin = User.builder().createdAt(LocalDateTime.now())
                .username("admin")
                .email("adminEmail@gmail.com")
                .password(passwordEncoder.bCryptPasswordEncoder().encode("admin"))
                .rol(UserRol.ADMINISTRADOR)
                .isEnabled(true).build();
        userRepository.save(admin);


        User user = User.builder().createdAt(LocalDateTime.now())
                .username("user")
                .email("useremail@gmail.com")
                .password(passwordEncoder.bCryptPasswordEncoder().encode("user"))
                .rol(UserRol.USER)
                .isEnabled(true)
                .build();
        userRepository.save(user);


        User invitado = User.builder().createdAt(LocalDateTime.now())
                .username("invitado")
                .email("invitadoEmail@gmail.com")
                .password(passwordEncoder.bCryptPasswordEncoder().encode("invitado"))
                .rol(UserRol.INVITADO)
                .isEnabled(true)
                .build();
        userRepository.save(invitado);


        User superadmin = User.builder().createdAt(LocalDateTime.now())
                .username("superadmin")
                .email("superadminEmail@gmail.com")
                .password(passwordEncoder.bCryptPasswordEncoder().encode("superadmin"))
                .rol(UserRol.SUPERADMINISTRADOR)
                .isEnabled(true)
                .build();
        userRepository.save(superadmin);



    }
}
