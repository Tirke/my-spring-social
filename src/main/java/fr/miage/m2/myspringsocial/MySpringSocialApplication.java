package fr.miage.m2.myspringsocial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MySpringSocialApplication {

  public static void main(String[] args) {
    SpringApplication.run(MySpringSocialApplication.class, args);
  }
}
