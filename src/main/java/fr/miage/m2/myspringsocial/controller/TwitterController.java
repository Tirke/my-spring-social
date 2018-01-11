package fr.miage.m2.myspringsocial.controller;

import fr.miage.m2.myspringsocial.domain.Event;
import fr.miage.m2.myspringsocial.domain.EventRepository;
import fr.miage.m2.myspringsocial.domain.EventType;
import fr.miage.m2.myspringsocial.domain.SocialMedia;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.MediaEntity;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TwitterController {


  private Twitter twitter;
  private ConnectionRepository connectionRepo;

  @Autowired
  EventRepository eventRepository;

  @Autowired
  public TwitterController(Twitter twitter, ConnectionRepository connectionRepo) {
    this.twitter = twitter;
    this.connectionRepo = connectionRepo;
  }

  @GetMapping("/twitter")
  public String helloTwitter(Model model) {
    if (connectionRepo.findPrimaryConnection(Twitter.class) == null) {
      return "redirect:/connect/twitter";
    }

    twitter.timelineOperations().getUserTimeline().forEach(t -> {

      Event event = buildEvent(t);

      if (getEventType(t).equals(EventType.share)) {
        event = buildEvent(t.getRetweetedStatus());
        event.setEventType(EventType.share);
      } else {
        event = buildEvent(t);

        Event linkedTo = new Event();

        if (getEventType(t).equals(EventType.comment)) {
          linkedTo = buildEvent(twitter.timelineOperations().getStatus(t.getInReplyToStatusId()));
          event.setLinkedTo(linkedTo.getId());
          eventRepository.save(linkedTo);
        }
      }

      eventRepository.save(event);


    });

    twitter.timelineOperations().getFavorites().forEach(t -> {
      Event event = buildEvent(t);
      event.setEventType(EventType.like);
      eventRepository.save(event);
    });

    return "index";
  }

  private Event buildEvent(Tweet tweet) {
    Event event = new Event()
        .setId(tweet.getId())
        .setDate(tweet.getCreatedAt())
        .setSocialMedia(SocialMedia.twitter)
        .setEventType(getEventType(tweet))
        .setContent(tweet.getText())
        .setAuthor(tweet.getFromUser())
        .setMedias(tweet.getEntities().getMedia().stream().map(MediaEntity::getMediaUrl).collect(
            Collectors.toSet()));
    return event;
  }

  private EventType getEventType(Tweet tweet) {
    if (tweet.isRetweet()) {
      return EventType.share;
    }
    if (tweet.getInReplyToUserId() != null) {
      return EventType.comment;
    }
    return EventType.post;
  }
}
