package fr.miage.m2.myspringsocial.event;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, EventId> {

  @Query("SELECT e FROM Event e WHERE e.forUser =?1 AND (e.author = null or e.eventType in ?2 )ORDER BY e.date DESC")
  List<Event> getEventsForUser(String user, List<EventType> eventTypes);

  @Query("SELECT max(e.id) FROM Event e WHERE e.socialMedia=?1 AND e.eventType <> ?2 AND e.forUser=?3")
  String findLastId(SocialMedia socialMedia, EventType eventType, String user);

  @Query("SELECT e.linkedTo FROM Event e WHERE e.socialMedia=?1 AND e.eventType = ?2 AND e.forUser=?3")
  List<String> getLinkedTo(SocialMedia socialMedia, EventType eventType, String user);

  @Query("SELECT max(e.date) FROM Event e WHERE e.socialMedia=?1 AND e.eventType IN ?2 AND e.forUser=?3")
  Date getMaxDate(SocialMedia socialMedia, List<EventType> eventType, String user);

  @Query("SELECT max(e.date) FROM Event e WHERE e.linkedTo=?1 AND e.socialMedia=?2 AND e.eventType IN ?3 AND e.forUser=?4")
  Date getMaxDateFromEvent(Event eventId, SocialMedia socialMedia, List<EventType> eventType,
      String user);

  @Query("SELECT e.id FROM Event e WHERE e.socialMedia=?1 AND e.eventType <> ?2 AND e.forUser=?3 AND e.author=null")
  List<String> getAllId(SocialMedia socialMedia, EventType eventType, String user);

  @Query("SELECT e.author FROM Event e WHERE e.socialMedia=?1 AND e.eventType = ?2 AND e.forUser=?3 AND e.linkedTo=?4")
  List<String> getIdLinkedTo(SocialMedia socialMedia, EventType eventType, String user,
      Event linkedTo);

}
