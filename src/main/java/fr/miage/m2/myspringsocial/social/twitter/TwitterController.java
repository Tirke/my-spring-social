package fr.miage.m2.myspringsocial.social.twitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TwitterController {

  private Twitter twitter;
  private ConnectionRepository connectionRepo;

  @Autowired
  public TwitterController(Twitter twitter, ConnectionRepository connectionRepo) {
    this.twitter = twitter;
    this.connectionRepo = connectionRepo;
  }

  @GetMapping("/profile/twitter")
  public String twitterProfile(Model model) {
    if (connectionRepo.findPrimaryConnection(Twitter.class) == null) {
      return "redirect:/connect/twitter";
    }

    model.addAttribute("profile", twitter.userOperations().getUserProfile());
    return "twitter/profile";
  }


}
