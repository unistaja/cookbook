package ee.cookbook.security;

import ee.cookbook.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
@Component
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
  @Autowired
  private CookbookUserDetailsService userDetailsService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsServiceBean())
        .passwordEncoder(new BCryptPasswordEncoder());

  }

  @Override
  public UserDetailsService userDetailsServiceBean() throws Exception {
    return userDetailsService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
        .antMatchers("/api/admin/**").hasRole(User.ROLE_ADMIN)
        .anyRequest().authenticated()
        .and()
      .formLogin().and()
      .httpBasic();
    //TODO: figure out how to actually handle this
    http.csrf().disable();
  }
}
