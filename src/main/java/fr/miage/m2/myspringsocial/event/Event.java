package fr.miage.m2.myspringsocial.event;

import java.util.Date;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity
@IdClass(EventId.class)
public class Event {

  // @Autowired
  // EventRepository eventRepository;

  @Id
  private String id;
  @Id
  @Enumerated(EnumType.STRING)
  private SocialMedia socialMedia;
  @Enumerated(EnumType.STRING)
  private EventType eventType;
  private String content;
  private String author;
  private Date date;
  @ElementCollection
  private Set<String> medias;
  private String linkedTo;

  // public Event getLinkedTo() {
  //  return eventRepository.findOne(new EventId().setId(linkedTo).setSocialMedia(this.socialMedia));
  //}

}
