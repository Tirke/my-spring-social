package fr.miage.m2.myspringsocial.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, EventId> {


  @Query("SELECT max(e.id) FROM Event e WHERE e.socialMedia=?1 AND e.eventType <> ?2 GROUP BY e.socialMedia")
  String findLastId(SocialMedia socialMedia, EventType eventType);

}
