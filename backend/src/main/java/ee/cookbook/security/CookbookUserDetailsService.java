package ee.cookbook.security;

import ee.cookbook.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

@Transactional
public class CookbookUserDetailsService implements UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    EntityManager em;

    public CookbookUserDetailsService(EntityManager em) {
        this.em = em;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            return em.createQuery("select u from User u where u.username=:username", User.class).setParameter("username", s).getSingleResult();
        }
        catch (PersistenceException e) {
            log.error("Unable to find user", e);
            throw new UsernameNotFoundException("Unable to find user");
        }
    }
}
