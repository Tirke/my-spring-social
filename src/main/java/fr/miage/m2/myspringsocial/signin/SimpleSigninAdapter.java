package fr.miage.m2.myspringsocial.signin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * This Adapter is used when an already known user signs in with Facebook Or Twitter
 */
@Slf4j
public class SimpleSigninAdapter implements SignInAdapter {

  private final RequestCache cache;


  public SimpleSigninAdapter(RequestCache cache) {
    this.cache = cache;
  }

  @Override
  public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
    log.info("Signin in adapter with already known user: " + userId);
    SigninUtils.signin(userId);
    return null;
  }
}
