package fr.miage.m2.myspringsocial.config;

import fr.miage.m2.myspringsocial.service.AccountDetailsService;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private AccountDetailsService accountDetailsService;

  @Autowired
  public SecurityConfig(
      AccountDetailsService accountDetailsService) {
    this.accountDetailsService = accountDetailsService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
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
        .failureUrl("/signin?param.error=bad_credentials")
        .and().logout().logoutUrl("/signout").deleteCookies("JSESSIONID")
        .and().authorizeRequests()
        .antMatchers("/", "/favicon.ico",
            "/auth/**", "/signin/**", "/signup/**", "/disconnect/facebook").permitAll()
        .antMatchers("/**").authenticated()
        .and().rememberMe();
  }
}
