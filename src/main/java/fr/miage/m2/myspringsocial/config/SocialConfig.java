package fr.miage.m2.myspringsocial.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;

@Configuration
@EnableSocial
// We are using the adapter because Spring Boot already configures interesting things for us
// Like the providers ...
public class SocialConfig extends SocialConfigurerAdapter {

  private final DataSource dataSource;

  @Autowired
  public SocialConfig(DataSource dataSource) {
    this.dataSource = dataSource;
  }


  @Override
  public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator locator) {
    return new JdbcUsersConnectionRepository(dataSource, locator, Encryptors.noOpText());
  }

  @Bean
  public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator locator,
      UsersConnectionRepository userCoRepo) {
    return new ProviderSignInUtils(locator, userCoRepo);
  }
}
