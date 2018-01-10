package fr.miage.m2.myspringsocial.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

public class SimpleSigninAdapter implements SignInAdapter {

  private final RequestCache cache;


  public SimpleSigninAdapter(RequestCache cache) {
    this.cache = cache;
  }

  @Override
  public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(userId, null, null));
    return extractOriginalUrl(request);
  }

  private String extractOriginalUrl(NativeWebRequest request) {
    HttpServletRequest nativeReq = request.getNativeRequest(HttpServletRequest.class);
    HttpServletResponse nativeRes = request.getNativeResponse(HttpServletResponse.class);
    SavedRequest saved = cache.getRequest(nativeReq, nativeRes);
    if (saved == null) {
      return null;
    }
    cache.removeRequest(nativeReq, nativeRes);
    removeAutheticationAttributes(nativeReq.getSession(false));
    return saved.getRedirectUrl();
  }

  private void removeAutheticationAttributes(HttpSession session) {
    if (session == null) {
      return;
    }
    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
  }
}
