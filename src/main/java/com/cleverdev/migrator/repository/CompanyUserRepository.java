package com.cleverdev.migrator.repository;

import com.cleverdev.migrator.model.CompanyUser;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.retry.annotation.Retryable;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    @Modifying @Query(value = "INSERT INTO CompanyUser (login) values (?1) ON CONFLICT DO NOTHING")
    @Retryable // in a multi-threaded env sometime causes deadlocks, so we retry
    int createByLoginIfNotExists(String login);

    List<CompanyUser> findAllByLoginIn(Collection<String> logins);
}
