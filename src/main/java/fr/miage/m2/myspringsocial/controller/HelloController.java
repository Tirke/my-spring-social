package fr.miage.m2.myspringsocial.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

  private Facebook facebook;
  private ConnectionRepository connectionRepo;

  @Autowired
  public HelloController(Facebook facebook,
      ConnectionRepository connectionRepo) {
    this.facebook = facebook;
    this.connectionRepo = connectionRepo;
  }

  @GetMapping("/")
  public String fetchData(Model model) {

    if (connectionRepo.findPrimaryConnection(Facebook.class) == null) {
      return "redirect:/connect/facebook";
    }

//    System.out.println(facebook.userOperations().getUserProfile());
//    System.out.println(facebook.feedOperations().getPosts());
//    System.out.println(facebook.feedOperations().getHomeFeed());
//    System.out.println(facebook.feedOperations().getFeed());
    model.addAttribute("profile", facebook.userOperations().getUserProfile());
//    model.addAttribute("feed", facebook.feedOperations().getFeed());
    return "index";
  }

//  @GetMapping("/FBcb")
//  public String wtf(@RequestParam(value = "code") String oauthCode, HttpSession session) throws FacebookException {
//    Facebook facebook = (Facebook) session.getAttribute("facebook");
//    AccessToken token = facebook.getOAuthAccessToken(oauthCode);
//    System.out.println(token);
//    System.out.println(facebook.permissions().getPermissions());
//    System.out.println(facebook.getHome());
//    System.out.println("im in cb");
//    return "index";
//  }

}
