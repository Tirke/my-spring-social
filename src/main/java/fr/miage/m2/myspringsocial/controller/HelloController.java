package fr.miage.m2.myspringsocial.controller;


import org.springframework.stereotype.Controller;

@Controller
public class HelloController {

  /*private Facebook facebook;
  private Twitter twitter;
  private Instagram instagram;
  private ConnectionRepository connectionRepo;
  private InstagramService service;

  @Autowired
  public HelloController(Facebook facebook,
      Twitter twitter,
      ConnectionRepository connectionRepo) {
    this.facebook = facebook;
    this.twitter = twitter;
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


  @GetMapping("/insta")
  public String helloInsta(Model model) {

    if (instagram == null) {
      return "connect/instagramConnect";
    }

    return "index";
  }

  @PostMapping("/connect/instagram")
  public String connectToInstagram() {
    service = new InstagramAuthService()
        .apiKey("1241d3b7ad774a598f42e99b51935873")
        .apiSecret("83d9ba90e5e249e190238e0a2d312e0b")
        .callback("http://localhost:8080/connect/instagram")
        .build();
    String authorizationUrl = service.getAuthorizationUrl();
    return "redirect:" + authorizationUrl;
  }

  @GetMapping("/connect/instagram")
  public String getInstaToken(@RequestParam String code) throws InstagramException {
    Verifier verifier = new Verifier(code);
    Token accessToken = service.getAccessToken(verifier);
    instagram = new Instagram(accessToken);
    System.out.println(instagram.getCurrentUserInfo().getData().getId());
    return "connect/instagramConnected";
  }


  @GetMapping("/fb")
  public String helloFb(Model model) {

    if (connectionRepo.findPrimaryConnection(Facebook.class) == null) {
      return "redirect:/connect/facebook";
    }

    System.out.println(facebook.feedOperations().getFeed());
//    System.out.println(facebook.feedOperations().getHomeFeed());
    model.addAttribute("profile", facebook.userOperations().getUserProfile());
    return "index";
  }*/
}
