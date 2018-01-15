package fr.miage.m2.myspringsocial.home;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.miage.m2.myspringsocial.account.AccountDetails;
import fr.miage.m2.myspringsocial.config.CurrentUser;
import fr.miage.m2.myspringsocial.event.Event;
import fr.miage.m2.myspringsocial.event.EventRepository;
import fr.miage.m2.myspringsocial.event.EventType;
import fr.miage.m2.myspringsocial.social.fb.FacebookService;
import fr.miage.m2.myspringsocial.social.twitter.TwitterService;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@Slf4j
public class HomeController {

  private final EventRepository er;
  private final FacebookService facebookService;
  private final TwitterService twitterService;
  private List<EventType> eventTypes = Arrays.asList(EventType.SHARED_BY, EventType.LIKED_BY,
      EventType.COMMENTED_BY);

  @Autowired
  public HomeController(EventRepository er,
      FacebookService facebookService,
      TwitterService twitterService) {
    this.er = er;
    this.facebookService = facebookService;
    this.twitterService = twitterService;
  }


  @GetMapping("/")
  public String home(@CurrentUser AccountDetails user, Model model) {
    // CurrentUser is a way to retrieve the current user , null when anonymous
    if (user != null) {
      List<Event> events = er.getEventsForUser(user.getUserId(), eventTypes);
      events.forEach(Event::buildPresentation);
      model.addAttribute("events", events);
    }
    return "index";
  }

  @GetMapping("/refresh")
  public String refresh(@CurrentUser AccountDetails user) {
    facebookService.fetchRecent(user);
    twitterService.fetchRecent(user);
    return "redirect:";
  }

  @GetMapping(value = "/download", produces = "application/json")
  public ResponseEntity<Resource> download(@CurrentUser AccountDetails user) {
    List<Event> events = er.getEventsForUser(user.getUserId(), eventTypes);

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    String json = gson.toJson(events);

    ByteArrayResource resource = new ByteArrayResource(json.getBytes());
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=events.json")
        .contentLength(resource.contentLength())
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(resource);
  }

}
