package fr.miage.m2.myspringsocial.twitter;

import fr.miage.m2.myspringsocial.event.Event;
import fr.miage.m2.myspringsocial.event.EventRepository;
import fr.miage.m2.myspringsocial.event.EventType;
import fr.miage.m2.myspringsocial.event.SocialMedia;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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
  private EventRepository eventRepository;

  private final int numberFetched = 3;

  @Autowired
  public TwitterController(Twitter twitter, ConnectionRepository connectionRepo,
      EventRepository eventRepository) {
    this.twitter = twitter;
    this.connectionRepo = connectionRepo;
    this.eventRepository = eventRepository;
  }

  @GetMapping("/twitter")
  public String helloTwitter(Model model) {
    if (connectionRepo.findPrimaryConnection(Twitter.class) == null) {
      return "redirect:/connect/twitter";
    }

    if (eventRepository.findLastId(SocialMedia.twitter, EventType.like) == null) {
      saveEvents(twitter.timelineOperations().getUserTimeline());
    } else {
      //get the latest fetched tweet
      long sinceId = Long.valueOf(eventRepository.findLastId(SocialMedia.twitter, EventType.like));
      long maxId = 0;
      //get latest tweet since the last fetch
      List<Tweet> liste = twitter.timelineOperations()
          .getUserTimeline(numberFetched, sinceId, maxId);
      while (liste.size() > 0) {
        saveEvents(liste);
        Tweet tweet = liste.stream()
            .min(Comparator.comparing(Tweet::getId))
            .get();
        maxId = Long.valueOf(tweet.getId()) - 1;
        //check if there are still some tweet that need to be fetched
        liste = twitter.timelineOperations().getUserTimeline(numberFetched, sinceId, maxId);
      }
    }

    return "index";
  }

  private void saveEvents(List<Tweet> tweetList) {
    tweetList.forEach(t -> {

      Event linkedTo = null;

      if (getEventType(t).equals(EventType.share)) {
        linkedTo = buildEvent(t.getRetweetedStatus());
      }
      if (getEventType(t).equals(EventType.comment)) {
        linkedTo = buildEvent(
            twitter.timelineOperations().getStatus(t.getInReplyToStatusId()));
      }
      if (linkedTo != null) {
        eventRepository.save(linkedTo);
      }

      Event event = buildEvent(t).setLinkedTo(linkedTo == null ? null : linkedTo.getId());

      eventRepository.save(event);

    });
  }

  private void saveLike(List<Tweet> tweetList) {
    tweetList.forEach(t -> {
      Event linkedTo = buildEvent(t);

      Event event = new Event()
          .setId(UUID.randomUUID().toString())
          .setDate(
              t.getCreatedAt()) //a like doesn't have a date, we have to use the date of the liked tweet
          .setSocialMedia(SocialMedia.twitter)
          .setEventType(EventType.like)
          .setAuthor(twitter.userOperations().getScreenName())
          .setLinkedTo(linkedTo.getId());

      eventRepository.save(linkedTo);
      eventRepository.save(event);

    });
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
    if (tweet.getInReplyToStatusId() != null) {
      return EventType.comment;
    }
    return EventType.post;
  }

}
