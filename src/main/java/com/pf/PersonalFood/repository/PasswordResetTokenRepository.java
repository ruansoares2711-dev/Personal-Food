package com.pf.PersonalFood.repository;

import com.pf.PersonalFood.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    // Método crucial para buscar o token que vem na URL do e-mail
    Optional<PasswordResetToken> findByToken(String token);
}