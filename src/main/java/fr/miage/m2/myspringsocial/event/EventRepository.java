package fr.miage.m2.myspringsocial.event;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, EventId> {


  List<Event> findAllByForUser(String user);

  @Query("SELECT max(e.id) FROM Event e WHERE e.socialMedia=?1 AND e.eventType <> ?2 AND e.forUser=?3")
  String findLastId(SocialMedia socialMedia, EventType eventType, String user);

  @Query("SELECT e.linkedTo FROM Event e WHERE e.socialMedia=?1 AND e.eventType = ?2 AND e.forUser=?3")
  List<String> getLinkedTo(SocialMedia socialMedia, EventType eventType, String user);

}
