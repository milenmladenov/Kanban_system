package com.tusofia.diplomna.controller.social;

import com.tusofia.diplomna.exception.StorageFileNotFoundException;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.image.StorageService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UploadAvatarController {

  private final StorageService storageService;

  @Autowired private UserService userService;

  @Autowired
  public UploadAvatarController(StorageService storageService) {
    this.storageService = storageService;
  }

  @GetMapping("/upload-avatar")
  public String uploadAvatarPage(
      Model model, Authentication authentication, HttpServletRequest req) {
    User userLogged = userService.findByUser(authentication.getName());
    if (userLogged == null) {
      return "redirect:/login";
    }
    model.addAttribute("loggedUser", userLogged);
    userService.updateUserAttributes(userLogged, req);
    return "/upload-avatar";
  }

  @PostMapping("/upload-avatar")
  // A method that handles the upload of the avatar.
  public String handleFileUpload(
      // A Spring class that represents a file uploaded through a multipart request.
      @RequestParam("file") MultipartFile file, Authentication authentication) {
    User user = userService.findByUser(authentication.getName());
    if (storageService.isCorrectImageType(file)) {
      storageService.store(file, user.getId());
      return "redirect:/upload-avatar?success";
    } else {
      return "redirect:/upload-avatar?wrongtype";
    }
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }
}
