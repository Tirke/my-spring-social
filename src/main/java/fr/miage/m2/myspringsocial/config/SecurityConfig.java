package fr.miage.m2.myspringsocial.config;

import fr.miage.m2.myspringsocial.service.AccountDetailsService;
import fr.miage.m2.myspringsocial.service.SocialAccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private SocialAccountDetailsService socialAccountDetailsService;
  private AccountDetailsService accountDetailsService;

  @Autowired
  public SecurityConfig(
      SocialAccountDetailsService socialAccountDetailsService,
      AccountDetailsService accountDetailsService) {
    this.socialAccountDetailsService = socialAccountDetailsService;
    this.accountDetailsService = accountDetailsService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SocialUserDetailsService socialUserDetailsService() {
    return new SocialAccountDetailsService();
  }


  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(accountDetailsService).passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web
        .ignoring()
        .antMatchers("/**/*.css", "/**/*.png", "/**/*.gif", "/**/*.jpg");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .formLogin().loginPage("/signin")
        .loginProcessingUrl("/signin/authenticate")
        .defaultSuccessUrl("/connect")
        .failureUrl("/signin?error=bad_credentials")
        .and().logout().logoutUrl("/signout").deleteCookies("JSESSIONID", "SESSION")
        .and().authorizeRequests()
        .antMatchers("/", "/favicon.ico",
            "/auth/**", "/signin/**", "/signup/**", "/disconnect/facebook").permitAll()
        .antMatchers("/**").authenticated()
        .and().rememberMe();
  }
}
