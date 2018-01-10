package fr.miage.m2.myspringsocial.service;

import fr.miage.m2.myspringsocial.domain.Account;
import fr.miage.m2.myspringsocial.domain.AccountRepository;
import fr.miage.m2.myspringsocial.validation.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private AccountRepository accountRepo;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public AccountService(AccountRepository accountRepo,
      PasswordEncoder passwordEncoder) {
    this.accountRepo = accountRepo;
    this.passwordEncoder = passwordEncoder;
  }

  public Account getByUsername(String username) {
    return accountRepo.getByUsername(username);
  }

  public Account create(UserForm form) throws UsernameNotUnique {
    if (accountRepo.existsByUsername(form.getUsername())) {
      throw new UsernameNotUnique("An user with the same username already exists");
    }

    Account account = new Account();
    String password = passwordEncoder.encode(form.getPassword());
    account.setFirstName(form.getFirstName()).setLastName(form.getLastName())
        .setUsername(form.getUsername()).setPassword(password);

    return accountRepo.save(account);
  }
}
