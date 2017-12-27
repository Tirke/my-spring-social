package fr.miage.m2.myspringsocial.controller;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HelloController {

//  private Facebook facebook;
//  private ConnectionRepository connectionRepo;
//
//  @Autowired
//  public HelloController(Facebook facebook,
//      ConnectionRepository connectionRepo) {
//    this.facebook = facebook;
//    this.connectionRepo = connectionRepo;
//  }

  @GetMapping("/")
  public RedirectView fetchData(HttpSession session) throws FacebookException {
    Facebook facebook = new FacebookFactory().getInstance();
    session.setAttribute("facebook", facebook);
    RedirectView redirectView = new RedirectView();
    redirectView.setUrl(facebook.getOAuthAuthorizationURL("http://localhost:8080/FBcb"));
    return redirectView;
//    ResponseList<Post> feed = facebook.getHome();
//    System.out.println(feed);

//    if (connectionRepo.findPrimaryConnection(Facebook.class) == null) {
//      return "redirect:/connect/facebook";
//    }
//
//    model.addAttribute("profile", facebook.userOperations().getUserProfile());
////    model.addAttribute("feed", facebook.feedOperations().getFeed());
//    return "index";
  }

  @GetMapping("/FBcb")
  public String wtf(HttpSession session) throws FacebookException {
    Facebook facebook = (Facebook) session.getAttribute("facebook");
    System.out.println(facebook.permissions().getPermissions());
    System.out.println(facebook.getHome());
    System.out.println("im in cb");
    return "index";
  }

}
