package ee.cookbook.controller;

import ee.cookbook.dao.UserRepository;
import ee.cookbook.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
  private UserRepository userRepository;

  @RequestMapping(method = RequestMethod.GET)
  public User getCurrentUser(Authentication auth) {
    return (User) auth.getPrincipal();
  }

  @RequestMapping(method = RequestMethod.POST)
  public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Authentication auth) {
    User currentUser = (User) auth.getPrincipal();
    BCryptPasswordEncoder hasher = new BCryptPasswordEncoder();
    if (hasher.matches(oldPassword, currentUser.passwordHash)) {
      currentUser.passwordHash = hasher.encode(newPassword);
      userRepository.save(currentUser);
      return "OK";
    }
    return "ERROR";
  }

}
