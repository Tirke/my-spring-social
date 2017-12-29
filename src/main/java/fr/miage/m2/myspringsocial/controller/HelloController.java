package fr.miage.m2.myspringsocial.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.NewShare;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

  private Facebook facebook;
  private Twitter twitter;
  private LinkedIn linkedIn;
  private ConnectionRepository connectionRepo;

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
    };
    System.out.println(linkedIn.networkUpdateOperations().getCurrentShare().getContent().getTitle().toString());
    System.out.println(linkedIn.profileOperations().getProfileUrl());
    return "index";
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
