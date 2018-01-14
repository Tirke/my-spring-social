package fr.miage.m2.myspringsocial.fb;

import fr.miage.m2.myspringsocial.account.AccountDetails;
import fr.miage.m2.myspringsocial.config.CurrentUser;
import fr.miage.m2.myspringsocial.event.Event;
import fr.miage.m2.myspringsocial.event.EventId;
import fr.miage.m2.myspringsocial.event.EventRepository;
import fr.miage.m2.myspringsocial.event.EventType;
import fr.miage.m2.myspringsocial.event.SocialMedia;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FacebookController {

  private Facebook facebook;
  private ConnectionRepository connectionRepo;
  private EventRepository eventRepository;

  private final int max = 1;

  @Autowired
  public FacebookController(Facebook facebook,
      ConnectionRepository connectionRepo, EventRepository eventRepository) {
    this.facebook = facebook;
    this.connectionRepo = connectionRepo;
    this.eventRepository = eventRepository;
  }


  @GetMapping("/profile/facebook")
  public String facebookProfile(Model model) {
    if (connectionRepo.findPrimaryConnection(Facebook.class) == null) {
      return "redirect:/connect/facebook";
    }

    Integer totalFriends = facebook.friendOperations().getFriendIds().getTotalCount();
    User profile = facebook.userOperations().getUserProfile();
    model.addAttribute("profile", profile);
    model.addAttribute("age", getUserAge(profile));
    model.addAttribute("totalFriends", totalFriends);

    return "facebook/profile";
  }

  private int getUserAge(User profile) {
    LocalDate birthday = LocalDate.parse(profile.getBirthday(),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    return Period.between(birthday, LocalDate.now()).getYears();
  }

  @GetMapping("/fb")
  public String helloFb(Model model, @CurrentUser AccountDetails user) {
    if (connectionRepo.findPrimaryConnection(Facebook.class) == null) {
      return "redirect:/connect/facebook";
    }

    List<EventType> type = new ArrayList<>();
    type.add(EventType.POST);
    type.add(EventType.SHARE);
    //get the latest post' date we have
    Date date = eventRepository.getMaxDate(SocialMedia.FACEBOOK, type, user.getUserId());
    Long since = null;
    if (date != null) {
      since = date.getTime() / 1000;
    }

    List<Post> liste = facebook.feedOperations()
        .getPosts(
            new PagingParameters(max, 0, since, new Date().getTime() / 1000));

    while (liste.size() > 0) {
      saveEvent(liste, user.getUserId());

      Post minPost = liste.stream().min(Comparator.comparing(Post::getCreatedTime)).get();

      Calendar cal = Calendar.getInstance();
      cal.setTime(minPost.getCreatedTime());
      cal.add(Calendar.SECOND, -1);

      liste = facebook.feedOperations()
          .getPosts(
              new PagingParameters(2, 0, since, cal.getTime().getTime() / 1000));

    }

    // on cherche tous les posts pour voir si des commentaires ont été ajoutés
    List<String> ids = eventRepository
        .getAllId(SocialMedia.FACEBOOK, EventType.LIKE, user.getUserId());
    ids.forEach(id -> {
      saveComments(id, user.getUserId());
    });

    return "index";
  }

  private void saveComments(String id, String user) {
    List<EventType> eventTypes = new ArrayList<>();
    eventTypes.add(EventType.COMMENT);
    Date dateComment = eventRepository
        .getMaxDateFromEvent(eventRepository.findOne(new EventId().setId(id).setSocialMedia(SocialMedia.FACEBOOK)), SocialMedia.FACEBOOK, eventTypes, user);

    Long since2 = null;
    if (dateComment != null) {
      since2 = dateComment.getTime() / 1000;
    }

    List<Comment> comments = facebook.commentOperations()
        .getComments(
            id,
            new PagingParameters(25, 0, since2, new Date().getTime() / 1000)
        );

    while (comments.size() > 0) {
      saveComment(id, comments, user);

      Comment minComment = comments.stream().min(Comparator.comparing(Comment::getCreatedTime))
          .get();

      Calendar cal = Calendar.getInstance();
      cal.setTime(minComment.getCreatedTime());
      cal.add(Calendar.SECOND, -1);

      comments = facebook.commentOperations()
          .getComments(
              id,
              new PagingParameters(25, 0, since2, cal.getTime().getTime() / 1000)
          );


    }
  }

  private boolean isShare(Post post) {
    return post.getName() != null && post.getDescription() != null;
  }


  private void saveEvent(List<Post> liste, String user) {
    liste.forEach(post -> {

      Set<String> media = new HashSet<>();
      if (post.getPicture() != null) {
        media.add(post.getPicture());
      }

      Event linkedTo = null;

      if (isShare(post)) {
        linkedTo = new Event()
            .setSocialMedia(SocialMedia.FACEBOOK)
            .setId(UUID.randomUUID().toString())
            .setForUser(user)
            .setEventType(EventType.POST)
            .setAuthor(post.getName())
            .setDate(post.getCreatedTime())
            .setMedias(media)
            .setContent(post.getDescription());
        eventRepository.save(linkedTo);
      }

      if (post.getMessage() != null) {
        Event event = new Event()
            .setSocialMedia(SocialMedia.FACEBOOK)
            .setId(post.getId())
            .setForUser(user)
            .setEventType(isShare(post) ? EventType.SHARE : EventType.POST)
            .setAuthor(null)
            .setDate(post.getCreatedTime())
            .setMedias(isShare(post) ? null : media)
            .setContent(post.getMessage())
            .setLinkedTo(isShare(post) ? linkedTo : null);

        eventRepository.save(event);
      }

    });
  }

  private void saveComment(String linkedTo, List<Comment> comments, String user) {

    comments.forEach((Comment comment) -> {
      if (eventRepository.findOne(new EventId()
          .setId(comment.getId())
          .setSocialMedia(SocialMedia.FACEBOOK)) == null) {
        System.out.println(comment.getFrom().getId());
        System.out.println(facebook.userOperations().getUserProfile().getId());
        String author =
            comment.getFrom().getId().equals(facebook.userOperations().getUserProfile().getId())
                ? null
                : comment.getFrom().getName();
        Event event = new Event()
            .setSocialMedia(SocialMedia.FACEBOOK)
            .setId(comment.getId())
            .setForUser(user)
            .setEventType(EventType.COMMENTED_BY)
            .setAuthor(author)
            .setDate(comment.getCreatedTime())
            .setMedias(null)
            .setContent(comment.getMessage())
            .setLinkedTo(eventRepository.findOne(new EventId()
                .setId(linkedTo)
                .setSocialMedia(SocialMedia.FACEBOOK)));
        eventRepository.save(event);
      }


    });
  }
}
