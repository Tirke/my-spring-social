package fr.miage.m2.myspringsocial.signin;

import fr.miage.m2.myspringsocial.account.Account;
import fr.miage.m2.myspringsocial.account.AccountDetails;
import fr.miage.m2.myspringsocial.account.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * This Adapter is used when an already known user (already used Fb or Twitter provider)
 * signs in with Facebook Or Twitter.
 */
@Slf4j
@Component
public class SimpleSigninAdapter implements SignInAdapter {

  @Autowired
  private AccountService accountService;

  @Override
  public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
    log.info("Signin in adapter with already known user: " + userId);
    Account account = accountService.getByUsername(userId);
    SigninUtils.signin(new AccountDetails(account));
    return null;
  }
}
