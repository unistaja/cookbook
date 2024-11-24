package ee.cookbook.controller;

import ee.cookbook.dao.UserRepository;
import ee.cookbook.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  @Autowired
  private UserRepository userRepository;

  @RequestMapping(value = "/addUser", method = RequestMethod.POST)
  public ResponseEntity addUser(@RequestParam String username, @RequestParam String password) {

    if (username == null || username.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("Kasutajanimi ei tohi olla tühi.");
    }
    if (password == null || password.isEmpty()) {
      return ResponseEntity.badRequest().body("Parool ei tohi olla tühi.");
    }
    BCryptPasswordEncoder hasher = new BCryptPasswordEncoder();
    User user = new User();
    user.username = username.trim();
    user.passwordHash = hasher.encode(password);
    user.active = true;
    userRepository.save(user);
    return ResponseEntity.ok("");
  }

  @RequestMapping(value = "/changeUserPassword", method = RequestMethod.POST)
  public ResponseEntity<String> changeUserPassword(@RequestParam String username, @RequestParam String password) {

    if (username == null || username.trim().isEmpty()) {
      return ResponseEntity.badRequest().body("Kasutajanimi ei tohi olla tühi.");
    }
    if (password == null || password.isEmpty()) {
      return ResponseEntity.badRequest().body("Parool ei tohi olla tühi.");
    }
    BCryptPasswordEncoder hasher = new BCryptPasswordEncoder();
    User user = userRepository.findByUsername(username.trim());
    user.passwordHash = hasher.encode(password);
    userRepository.save(user);
    return ResponseEntity.ok("");
  }
}
