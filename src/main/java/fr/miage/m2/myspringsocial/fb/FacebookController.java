package fr.miage.m2.myspringsocial.fb;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FacebookController {

  private Facebook facebook;


  @GetMapping("/profile/facebook")
  public String twitterProfile() {
    return "facebook/profile";
  }


}
