package fr.miage.m2.myspringsocial.signin;

import fr.miage.m2.myspringsocial.account.AccountDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SigninUtils {

  public static void signin(AccountDetails account) {
    SecurityContextHolder
        .getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities()));
  }

}
