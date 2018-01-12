package fr.miage.m2.myspringsocial.account;

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
  public SocialUserDetails loadUserByUserId(String username) throws UsernameNotFoundException {
    log.info("trying to access social user details :" + username);
    Account account = accountRepo.getByUsername(username);
    if (account == null) {
      throw new UsernameNotFoundException("Account not found");
    }

    return new AccountDetails(account);
  }
}
