package fr.miage.m2.myspringsocial.account;

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

  public Account create(AccountForm form) throws UsernameNotUnique {
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
