package fr.miage.m2.myspringsocial.domain;

import java.util.Collection;
import java.util.HashSet;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

public class AccountDetails implements SocialUserDetails {

  private Account account;

  public AccountDetails(Account account) {
    this.account = account;
  }

  @Override
  public String getUserId() {
    return account.getId();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new HashSet<>(1);
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    return authorities;
  }

  @Override
  public String getPassword() {
    return account.getPassword();
  }

  @Override
  public String getUsername() {
    return account.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}
