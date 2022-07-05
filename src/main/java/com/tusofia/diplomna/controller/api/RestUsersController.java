package com.tusofia.diplomna.controller.api;

import com.tusofia.diplomna.exception.StorageFileNotFoundException;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.image.StorageService;
import com.tusofia.diplomna.service.message.MessageService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RestUsersController {
  @Autowired private UserService userService;

  @Autowired private MessageService messageService;

  @Autowired private StorageService storageService;

  @GetMapping("/api/users")
  public List<User> getUsers() {
    List<User> users = userService.findAll();
    return users;
  }

  @GetMapping("/api/users/usernames")
  public List<String> getUsernames() {
    List<User> users = userService.findAll();
    ArrayList<String> usernames = new ArrayList<>();
    for (User user : users) {
      usernames.add(user.getUsername());
    }
    return usernames;
  }

  @GetMapping("/api/users/id/{id}")
  public User getUserById(@PathVariable Long id) {
    return userService.getById(id);
  }

  @GetMapping("/api/users/id/{id}/username")
  public String getUsernameById(@PathVariable Long id) {
    return userService.getById(id).getUsername();
  }

  @GetMapping("/api/users/username/{username}")
  public User getUserByUsername(@PathVariable String username) {
    return userService.findByUser(username);
  }

  @GetMapping("/api/user/newmessages")
  public int getUserNotifications(Authentication authentication) {
    if (authentication != null) {
      User userLogged = userService.findByUser(authentication.getName());
      return messageService.findByReceiverAndOpenedIs(userLogged, 0).size();
    } else {
      return 0;
    }
  }

  @GetMapping("/api/avatar/{id}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable Long id) {
    Resource file;
    try {
      file = storageService.loadAsResource(id.toString() + ".jpg");
    } catch (StorageFileNotFoundException e) {
      file = storageService.loadAsResource("0.jpg");
    }
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
        .body(file);
  }
}
