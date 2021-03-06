package com.tusofia.diplomna.config;

import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private UserService userService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider());
    String password = passwordEncoder().encode("admin");
    auth.inMemoryAuthentication().withUser("admin").password(password).roles("ADMIN");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic()
        .disable()
        .authorizeRequests()
        .antMatchers(
            // "/registration**",
            "/js/**", "/css/**", "/img/**", "/api/**", "/webjars/**")
        .permitAll()
        .antMatchers(
            "/",
            "/index",
            "/task-list",
            "/task-assign",
            "/tasks-pending",
            "/task-approve",
            "/task-deny",
            "/profile",
            "/upload-avatar",
            "/users",
            "/settings",
            "/appsettings",
            "/changepassword",
            "/messages",
            "/message",
            "/message-new",
            "/message-to",
            "/confirm-account",
            "/report-bug")
        .permitAll()
        .antMatchers("/admin/**")
        .hasAuthority("ADMIN")
        .and()
        .formLogin()
        .loginPage("/login")
        .and()
        .logout()
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .permitAll();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
    auth.setUserDetailsService(userService);
    auth.setPasswordEncoder(passwordEncoder());
    return auth;
  }
}
