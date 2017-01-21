package ee.cookbook.controller;

import ee.cookbook.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @RequestMapping(method = RequestMethod.GET)
  public User getCurrentUser(Authentication auth) {
    return (User) auth.getPrincipal();
  }
}
