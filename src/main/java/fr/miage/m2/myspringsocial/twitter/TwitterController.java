package fr.miage.m2.myspringsocial.twitter;

import fr.miage.m2.myspringsocial.account.AccountDetails;
import fr.miage.m2.myspringsocial.config.CurrentUser;
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

  private final int numberFetched = 3;
  private Twitter twitter;
  private ConnectionRepository connectionRepo;
  private EventRepository eventRepository;

  @Autowired
  public TwitterController(Twitter twitter, ConnectionRepository connectionRepo,
      EventRepository eventRepository) {
    this.twitter = twitter;
    this.connectionRepo = connectionRepo;
    this.eventRepository = eventRepository;
  }

  @GetMapping("/twitter")
  public String helloTwitter(Model model, @CurrentUser AccountDetails user) {
    if (connectionRepo.findPrimaryConnection(Twitter.class) == null) {
      return "redirect:/connect/twitter";
    }

    //get tweet, retweet and comment made by user
    if (eventRepository.findLastId(SocialMedia.TWITTER, EventType.LIKE, user.getUserId()) == null) {
      saveEvents(twitter.timelineOperations().getUserTimeline(), user.getUserId());
    } else {
      //get the latest fetched tweet
      long sinceId = Long.valueOf(
          eventRepository.findLastId(SocialMedia.TWITTER, EventType.LIKE, user.getUserId()));
      long maxId = 0;
      //get latest tweet since the last fetch
      List<Tweet> liste = twitter.timelineOperations()
          .getUserTimeline(numberFetched, sinceId, maxId);
      while (liste.size() > 0) {
        saveEvents(liste, user.getUserId());
        Tweet tweet = liste.stream()
            .min(Comparator.comparing(Tweet::getId))
            .get();
        maxId = Long.valueOf(tweet.getId()) - 1;
        //check if there are still some tweet that need to be fetched
        liste = twitter.timelineOperations().getUserTimeline(numberFetched, sinceId, maxId);
      }
    }

    //get the latest favorites
    List<Tweet> favorites = twitter.timelineOperations().getFavorites();

    List<String> alreadyFetched = eventRepository
        .getLinkedTo(SocialMedia.TWITTER, EventType.LIKE, user.getUserId());
    //remove from the favorites the one we already have
    favorites = favorites.stream().filter(tweet -> !alreadyFetched.contains(tweet.getId()))
        .collect(Collectors.toList());

    if (favorites.size() > 0) {
      saveLike(favorites, user.getUserId());
    }

    eventRepository.getAllId(SocialMedia.TWITTER, EventType.LIKE, user.getUserId())
        .forEach(s -> {
          twitter.timelineOperations().getRetweets(Long.valueOf(s)).forEach(tweet -> {
            Event event =buildEvent(tweet, user.getUserId()).setEventType(EventType.SHARED_BY).setLinkedTo(s);
            eventRepository.save(event);
          });
        });


    return "index";
  }


  @GetMapping("/profile/twitter")
  public String twitterProfile(Model model) {
    if (connectionRepo.findPrimaryConnection(Twitter.class) == null) {
      return "redirect:/connect/twitter";
    }

    model.addAttribute("profile", twitter.userOperations().getUserProfile());
    return "twitter/profile";
  }

  /**
   * Persist tweet, retweet and comment
   */
  private void saveEvents(List<Tweet> tweetList, String user) {
    tweetList.forEach(t -> {

      Event linkedTo = null;

      if (getEventType(t).equals(EventType.SHARE)) {
        linkedTo = buildEvent(t.getRetweetedStatus(), user);
      }
      if (getEventType(t).equals(EventType.COMMENT)) {
        linkedTo = buildEvent(
            twitter.timelineOperations().getStatus(t.getInReplyToStatusId()), user);
      }
      if (linkedTo != null) {
        eventRepository.save(linkedTo);
      }

      Event event = buildEvent(t, user).setLinkedTo(linkedTo == null ? null : linkedTo.getId())
          .setAuthor(null);

      eventRepository.save(event);

    });
  }

  /**
   * Persist favorites
   */
  private void saveLike(List<Tweet> tweetList, String user) {
    tweetList.forEach(t -> {
      Event linkedTo = buildEvent(t, user);

      Event event = new Event()
          .setId(UUID.randomUUID().toString()) //favorites are not new tweet, we add an id
          .setDate(
              t.getCreatedAt()) //a LIKE doesn't have a date, we have to use the date of the liked tweet
          .setSocialMedia(SocialMedia.TWITTER)
          .setEventType(EventType.LIKE)
          .setAuthor(null)
          .setLinkedTo(linkedTo.getId())
          .setForUser(user);
      ;

      eventRepository.save(linkedTo);
      eventRepository.save(event);

    });
  }


  /**
   * Create a new event based on tweet
   */
  private Event buildEvent(Tweet tweet, String user) {
    return new Event()
        .setId(tweet.getId())
        .setDate(tweet.getCreatedAt())
        .setSocialMedia(SocialMedia.TWITTER)
        .setEventType(getEventType(tweet))
        .setContent(tweet.getText())
        .setAuthor(
            tweet.getFromUser().equals(twitter.userOperations().getScreenName()) ? null
                : tweet.getFromUser())
        .setMedias(tweet.getEntities().getMedia().stream().map(MediaEntity::getMediaUrl).collect(
            Collectors.toSet()))
        .setForUser(user);

  }


  /**
   * Return which type of tweet this is
   */
  private EventType getEventType(Tweet tweet) {
    if (tweet.isRetweet()) {
      return EventType.SHARE;
    }
    if (tweet.getInReplyToStatusId() != null) {
      return EventType.COMMENT;
    }
    return EventType.POST;
  }

}
