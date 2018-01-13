package fr.miage.m2.myspringsocial.signup;

import fr.miage.m2.myspringsocial.account.Account;
import fr.miage.m2.myspringsocial.account.AccountDetails;
import fr.miage.m2.myspringsocial.account.AccountForm;
import fr.miage.m2.myspringsocial.account.AccountService;
import fr.miage.m2.myspringsocial.account.UsernameNotUnique;
import fr.miage.m2.myspringsocial.signin.SigninUtils;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    AccountForm user = new AccountForm();

    if (connection != null) {
      // This is when user chose a social provider for initial signup
      user = AccountForm.fromSocialProfile(connection.fetchUserProfile());
    } // Else go to signup form without any info

    model.addAttribute("user", user);
    return "signup";
  }

  @PostMapping("/signup")
  public String signupUser(@ModelAttribute @Valid AccountForm form, WebRequest request,
      BindingResult result) {
    try {
      Account account = accountService.create(form);
      AccountDetails accountDetails = new AccountDetails(account);
      SigninUtils.signin(accountDetails);
      signInUtils.doPostSignUp(account.getUsername(), request);
      return "redirect:/";
    } catch (UsernameNotUnique e) {
      return String.format("redirect:/error?message=%s", e.getMessage());
    }
  }
}
