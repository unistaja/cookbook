package ee.cookbook.security;

import ee.cookbook.dao.UserRepository;
import ee.cookbook.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

@Transactional
@Service
public class CookbookUserDetailsService implements UserDetailsService {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    try {
      User user = userRepository.findByUsername(s);
      if (user == null) {
        throw new UsernameNotFoundException("Unable to find user");
      }
      return user;
    } catch (PersistenceException e) {
      log.error("Unable to find user", e);
      throw new UsernameNotFoundException("Unable to find user");
    }
  }
}
