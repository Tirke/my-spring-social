package fr.miage.m2.myspringsocial.controller;


import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.exceptions.InstagramException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HelloController {

  private Facebook facebook;
  private Twitter twitter;
  private LinkedIn linkedIn;
  private Instagram instagram;
  private ConnectionRepository connectionRepo;
  private InstagramService service;

  @Autowired
  public HelloController(Facebook facebook,
      Twitter twitter, LinkedIn linkedIn,
      ConnectionRepository connectionRepo) {
    this.facebook = facebook;
    this.twitter = twitter;
    this.linkedIn = linkedIn;
    this.connectionRepo = connectionRepo;
  }

  @GetMapping("/twitter")
  public String helloTwitter(Model model) {
    if (connectionRepo.findPrimaryConnection(Twitter.class) == null) {
      return "redirect:/connect/twitter";
    }

    System.out.println(twitter.timelineOperations().getUserTimeline());
    // Renvoi 20 tweets max
    System.out.println(twitter.timelineOperations().getHomeTimeline());
    // L'appel de méthode avec la signature int,long,long permet de parcourir l'ensemble.
    // https://developer.twitter.com/en/docs/tweets/timelines/guides/working-with-timelines
    // J'utilise le home feed pr le moment car j'ai un compte tweeter tout neuf.
    System.out.println(twitter.timelineOperations().getHomeTimeline());

    twitter.timelineOperations().getHomeTimeline().forEach(t -> {
      System.out.println(t.getId() + " tweet id");
      System.out.println(t.getText() + " tweet text");
      System.out.println(t.getIdStr() + " tweet id string");
      System.out.println(t.getSource() + " tweet id source");
    });

    return "index";
  }

  @GetMapping("/linkedin")
  public String helloLinkedIn(Model model) {
    if (connectionRepo.findPrimaryConnection(LinkedIn.class) == null) {
      return "redirect:/connect/linkedin";
    }
    System.out.println(linkedIn.isAuthorized());
    System.out.println(linkedIn.profileOperations().getProfileId());
    System.out.println(linkedIn.networkUpdateOperations().getNetworkUpdates().toArray().toString());
    return "index";
  }

  @GetMapping("/insta")
  public void helloIsta(Model model) {

    service = new InstagramAuthService()
        .apiKey("1241d3b7ad774a598f42e99b51935873")
        .apiSecret("83d9ba90e5e249e190238e0a2d312e0b")
        .callback("http://localhost:8080/connect/instagram")
        .build();
    String authorizationUrl = service.getAuthorizationUrl();
    System.out.println(authorizationUrl);
  }

  @GetMapping("/connect/instagram")
  public void test(@RequestParam String code) throws InstagramException {
    System.out.println(code);
    Verifier verifier = new Verifier(code);
    Token accessToken = service.getAccessToken(verifier);
    instagram = new Instagram(accessToken);
    System.out.println(instagram.getCurrentUserInfo().getData().getId());
  }


  @GetMapping("/fb")
  public String helloFb(Model model) {

    if (connectionRepo.findPrimaryConnection(Facebook.class) == null) {
      return "redirect:/connect/facebook";
    }

    model.addAttribute("profile", facebook.userOperations().getUserProfile());
    return "index";
  }
}
