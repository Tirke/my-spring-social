package fr.miage.m2.myspringsocial.home;

import fr.miage.m2.myspringsocial.account.AccountDetails;
import fr.miage.m2.myspringsocial.config.CurrentUser;
import fr.miage.m2.myspringsocial.event.Event;
import fr.miage.m2.myspringsocial.event.EventRepository;
import fr.miage.m2.myspringsocial.event.EventType;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {


  @Autowired
  EventRepository er;


  @GetMapping("/")
  public String home(@CurrentUser AccountDetails user, Model model) {
    // CurrentUser is a way to retrieve the current user , null when anonymous
    if (user != null) {
      List<EventType> eventTypes = new ArrayList<>();
      eventTypes.add(EventType.SHARED_BY);
      eventTypes.add(EventType.LIKED_BY);
      eventTypes.add(EventType.COMMENTED_BY);
      List<Event> events = er.getEventsForUser(user.getUserId(), eventTypes);
      events.forEach(event -> event.buildPresentation());
      model.addAttribute("events", events);
    }
    return "index";
  }
}
