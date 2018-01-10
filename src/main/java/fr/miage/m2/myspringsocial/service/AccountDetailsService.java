package fr.miage.m2.myspringsocial.service;

import fr.miage.m2.myspringsocial.domain.Account;
import fr.miage.m2.myspringsocial.domain.AccountDetails;
import fr.miage.m2.myspringsocial.domain.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsService implements UserDetailsService {

  private AccountRepository accountRepo;

  @Autowired
  public AccountDetailsService(AccountRepository accountRepo) {
    this.accountRepo = accountRepo;
  }


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = accountRepo.getByUsername(username);
    if (account == null) {
      throw new UsernameNotFoundException("Account not found");
    }

    return new AccountDetails(account);
  }
}
