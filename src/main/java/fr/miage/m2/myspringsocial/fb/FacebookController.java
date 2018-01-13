package fr.miage.m2.myspringsocial.fb;

import fr.miage.m2.myspringsocial.account.AccountDetails;
import fr.miage.m2.myspringsocial.config.CurrentUser;
import fr.miage.m2.myspringsocial.event.Event;
import fr.miage.m2.myspringsocial.event.EventType;
import fr.miage.m2.myspringsocial.event.SocialMedia;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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

  @GetMapping("/fb")
  public String helloFb(Model model, @CurrentUser AccountDetails user) {
    if (connectionRepo.findPrimaryConnection(Facebook.class) == null) {
      return "redirect:/connect/facebook";
    }

    facebook.feedOperations().getPosts().forEach(post -> {

      Set<String> media = new HashSet<>();
      if (post.getPicture() != null) {
        media.add(post.getPicture());
      }

      Event linkedTo = new Event()
          .setSocialMedia(SocialMedia.FACEBOOK)
          .setId(UUID.randomUUID().toString())
          .setForUser(user.getUserId())
          .setEventType(EventType.POST)
          .setAuthor(post.getName())
          .setDate(post.getCreatedTime())
          .setMedias(media)
          .setContent(post.getDescription());

      Event event = new Event()
          .setSocialMedia(SocialMedia.FACEBOOK)
          .setId(post.getId())
          .setForUser(user.getUserId())
          .setEventType(post.getName() == null ? EventType.POST : EventType.SHARE)
          .setAuthor(null)
          .setDate(post.getCreatedTime())
          .setMedias(post.getName() == null ? media : null)
          .setContent(post.getMessage())
          .setLinkedTo(linkedTo.getId());

    });

    return "index";
  }


}
