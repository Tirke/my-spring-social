package fr.miage.m2.myspringsocial.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

  Account getByUsername(String username);

  boolean existsByUsername(String username);
}
