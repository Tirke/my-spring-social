package fr.miage.m2.myspringsocial.controller;

import fr.miage.m2.myspringsocial.config.CurrentUser;
import fr.miage.m2.myspringsocial.domain.AccountDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {


  @GetMapping("/")
  public String home(@CurrentUser AccountDetails user, Model model) {
    // CurrentUser is a way to retrieve the current user , null when anonymous
    return "index";
  }

}
