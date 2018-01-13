package fr.miage.m2.myspringsocial.fb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FacebookController {

  private Facebook facebook;
  private ConnectionRepository connectionRepo;

  @Autowired
  public FacebookController(Facebook facebook,
      ConnectionRepository connectionRepo) {
    this.facebook = facebook;
    this.connectionRepo = connectionRepo;
  }


  @GetMapping("/profile/facebook")
  public String facebookProfile(Model model) {
    if (connectionRepo.findPrimaryConnection(Facebook.class) == null) {
      return "redirect:/connect/facebook";
    }

    model.addAttribute("profile", facebook.userOperations().getUserProfile());

    return "facebook/profile";
  }


}
