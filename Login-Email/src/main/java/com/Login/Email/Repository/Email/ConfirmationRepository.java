package com.Login.Email.Repository.Email;


import com.Login.Email.Model.Email.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
    Confirmation findByToken(String token);

}
