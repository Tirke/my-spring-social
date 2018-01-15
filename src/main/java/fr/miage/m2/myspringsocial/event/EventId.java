package fr.miage.m2.myspringsocial.event;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class EventId implements Serializable {

  private String id;
  private SocialMedia socialMedia;
}
