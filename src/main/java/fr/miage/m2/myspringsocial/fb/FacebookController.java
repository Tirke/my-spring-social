package fr.miage.m2.myspringsocial.fb;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
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

    Integer totalFriends = facebook.friendOperations().getFriendIds().getTotalCount();
    User profile = facebook.userOperations().getUserProfile();
    model.addAttribute("profile", profile);
    model.addAttribute("age", getUserAge(profile));
    model.addAttribute("totalFriends", totalFriends);

    return "facebook/profile";
  }

  private int getUserAge(User profile) {
    LocalDate birthday = LocalDate.parse(profile.getBirthday(),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    return Period.between(birthday, LocalDate.now()).getYears();
  }


}
