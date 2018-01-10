package fr.miage.m2.myspringsocial.controller;

import fr.miage.m2.myspringsocial.domain.Account;
import fr.miage.m2.myspringsocial.service.AccountService;
import fr.miage.m2.myspringsocial.service.UsernameNotUnique;
import fr.miage.m2.myspringsocial.validation.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class SignupController {

  private ProviderSignInUtils signInUtils;
  private AccountService accountService;

  @Autowired
  public SignupController(ProviderSignInUtils signInUtils,
      AccountService accountService) {
    this.signInUtils = signInUtils;
    this.accountService = accountService;
  }


  @GetMapping("/signup")
  public String signupForm(Model model, WebRequest request) {
    Connection<?> connection = signInUtils.getConnectionFromSession(request);
    UserForm user = new UserForm();

    if (connection != null) {
      // This is when user chose a social provider for initial signup
      user = UserForm.fromSocialProfile(connection.fetchUserProfile());
    } // Else go to signup form without any info

    model.addAttribute("user", user);
    return "signup";
  }

  @PostMapping("/signup")
  public String signupUser(@ModelAttribute UserForm form, WebRequest request) {
    try {
      Account account = accountService.create(form);
      signInUtils.doPostSignUp(account.getUsername(), request);
      // Then our SignInAdapter in SocialConfig will trigger to effectively signin the user
      return "redirect:/";
    } catch (UsernameNotUnique e) {
      return String.format("redirect:/error?message=%s", e.getMessage());
    }
  }
}
