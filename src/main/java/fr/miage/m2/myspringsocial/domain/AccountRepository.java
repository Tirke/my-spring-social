package fr.miage.m2.myspringsocial.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

  Account getByUsername(String username);

  Account getById(String id);

  boolean existsByUsername(String username);
}
