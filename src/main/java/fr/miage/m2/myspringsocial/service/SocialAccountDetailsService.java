package fr.miage.m2.myspringsocial.service;

import fr.miage.m2.myspringsocial.domain.Account;
import fr.miage.m2.myspringsocial.domain.AccountDetails;
import fr.miage.m2.myspringsocial.domain.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SocialAccountDetailsService implements SocialUserDetailsService {

  @Autowired
  private AccountRepository accountRepo;


  @Override
  public SocialUserDetails loadUserByUserId(String id) throws UsernameNotFoundException {
    log.info("trying to access social user details :" + id);
    Account account = accountRepo.getById(id);
    if (account == null) {
      throw new UsernameNotFoundException("Account not found");
    }

    return new AccountDetails(account);
  }
}
