package ee.cookbook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Collections;

@Entity
public class User implements UserDetails {
    public static final String ROLE_ADMIN = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    public String username;

    @JsonIgnore
    public String passwordHash;

    public boolean active;

    public boolean isAdmin;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return isAdmin ? Collections.singleton(new SimpleGrantedAuthority("ROLE_" + ROLE_ADMIN)) : null;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return active;
    }
}
