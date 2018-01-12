package fr.miage.m2.myspringsocial.account;

import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.social.connect.UserProfile;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class AccountForm {

  @Size(min = 3, message = "at least 3 characters")
  private String username; // unique
  @Size(min = 6, message = "at least 6 characters")
  private String password;
  private String firstName;
  private String lastName;

  public static AccountForm fromSocialProfile(UserProfile profile) {
    AccountForm form = new AccountForm();
    form.setUsername(profile.getUsername()).setFirstName(profile.getFirstName())
        .setLastName(profile.getLastName());
    return form;
  }

}
