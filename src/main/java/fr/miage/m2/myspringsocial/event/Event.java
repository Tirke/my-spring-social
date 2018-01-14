package fr.miage.m2.myspringsocial.event;

import java.util.Date;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity
@IdClass(EventId.class)
public class Event {


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
  @ManyToOne
  private Event linkedTo;
  private String forUser;

  @Transient
  private String icone;

  @Transient
  private String description;


  public void buildPresentation(){
    switch (this.eventType){
      case LIKE:
        this.setIcone("favorite");
        this.setDescription("Vous avez aimé ");
        break;
      case COMMENT:
        this.setIcone("mode_comment");
        this.setDescription("Vous avez commenté ");
        break;
      case SHARE:
        this.setIcone("share");
        this.setDescription("Vous avez partagé ");
        break;
      case POST:
        this.setIcone("local_post_office");
        this.setDescription("Vous avez posté ");
    }
  }


}
