package com.tusofia.diplomna.controller.social;

import com.tusofia.diplomna.dto.MessageDTO;
import com.tusofia.diplomna.model.Message;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.service.message.MessageService;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;

@Controller
public class MessageController {
  @Autowired private UserService userService;

  @Autowired private MessageService messageService;

  public User getLoggedUser() {
    return userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @GetMapping("/messages")
  public String getInbox(HttpServletRequest req, Model model) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    List<Message> messages = messageService.getMessagesByUser(userLogged);
    messages.sort(Comparator.comparing(Message::getId).reversed());
    if (userLogged != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("messages", messages);
      userService.updateUserAttributes(userLogged, req);
    }
    return "messages";
  }

  @GetMapping("/message")
  public String openMessage(
      HttpServletRequest req, Model model, @RequestParam Long id, MessageDTO messageDto) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    List<Message> unreadMessages = messageService.findByReceiverAndOpenedIs(userLogged, 0);
    // Get username and messages
    if (userLogged != null) {
      model.addAttribute("loggedUser", userLogged);
      if (unreadMessages.size() >= 1) {
        req.getSession().setAttribute("unreadMessages", unreadMessages.size() - 1);
      }
    }
    if (id != null) {
      Message msg = messageService.getMessageById(id);
      model.addAttribute("message", msg);
      msg.setOpened(1);
      messageService.save(messageDto, userLogged.getId());
      return "message";
    } else {
      return "redirect:/messages";
    }
  }

  @DeleteMapping("/message-delete")
  public String deleteMessage(@RequestParam Long id) {
    User userLogged = getLoggedUser();
    List<Message> messages = messageService.getMessagesByUser(userLogged);
    if (messages.contains(messageService.getMessageById(id))) {
      messageService.deleteMessageById(id);
      return "redirect:/messages";
    } else {
      return "redirect:/messages?error";
    }
  }

  @GetMapping("/message-new")
  public String newMessage(Model model, Authentication authentication) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    if (userLogged != null) {
      model.addAttribute("loggedUser", userLogged);
      model.addAttribute("message", new Message());
    }
    return "message-new";
  }

  @PostMapping("/message-new")
  public String sendMessage(
      Model model, Message message, Authentication authentication, MessageDTO messageDto) {
    User userLogged = getLoggedUser();

    model.addAttribute("loggedUser", userLogged);

    User receiver = userService.findByUser(message.getReceiver().getUsername());
    if (message.getReceiver().getId() != userLogged.getId()) {
      if (receiver != null) {
        if (!(message.getSubject().isEmpty()) && !(message.getMessageText().isEmpty())) {
          messageService.save(messageDto, userLogged.getId());
          // Update statistics
          userService.incrementMessagesSentStats(userLogged);
          userService.incrementMessagesReceivedStats(receiver);
          return "redirect:/messages?success";
        } else {
          return "redirect:/message-new?emptytext";
        }
      } else {
        return "redirect:/message-new?notfound";
      }
    } else {
      return "redirect:/message-new?self";
    }
  }

  /**
   * If the user is logged in, and the user is not trying to send a message to themselves, then the user is redirected to
   * the message-to page
   *
   * @param model The model is an object that holds the data that you want to pass to the view.
   * @param authentication This is the object that contains the information about the currently logged in user.
   * @param message the message object that will be sent to the view
   * @param id the id of the user to whom the message is sent
   * @return A string
   */
  /**
   * If the user is logged in, and the user is not trying to send a message to themselves, then the user is redirected to
   * the message-to page
   *
   * @param model The model is an object that holds the data that you want to pass to the view.
   * @param message the message object that will be sent to the view
   * @param id the id of the user to whom the message is sent
   * @return A String
   */
  @GetMapping("/message-to")
  public String sendMessageToPage(Model model, Message message, @RequestParam Long id) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    User recepient = userService.getById(id);
    if (recepient != null) {
      if (userLogged.getId() != id) {
        model.addAttribute("recepient", recepient);
        if (userLogged != null) {
          model.addAttribute("loggedUser", userLogged);
          model.addAttribute("message", new Message());
        }
        message.setReceiver(recepient);
        return "message-to";
      } else {
        return "redirect:messages?self";
      }
    } else {
      return "redirect:messages?erroruser";
    }
  }

  /**
   * It takes a messageDTO object, a user id and a model object as parameters, and then it saves the messageDTO object to
   * the database, and then it increments the messages sent and received statistics of the logged in user and the user that
   * the message was sent to
   *
   * @param model The model object that will be passed to the view.
   * @param id The id of the user to whom the message is being sent.
   * @param messageDto the message object that will be sent to the target user
   * @return A redirect to the messages page with a success message.
   */
  @PostMapping("/message-to")
  public String sendMessageTo(Model model, @RequestParam Long id, MessageDTO messageDto) {
    User userLogged = getLoggedUser();
    model.addAttribute("loggedUser", userLogged);
    User target = userService.getById(id);
    if (target != null) {
      messageService.save(messageDto, target.getId());
      // Update statistics
      userService.incrementMessagesSentStats(userLogged);
      userService.incrementMessagesReceivedStats(target);
      return "redirect:/messages?success";
    } else {
      return "redirect:/message-to?error";
    }
  }

  @GetMapping("message-unread")
  public String unreadMessage(@RequestParam Long id) {
    User userLogged = getLoggedUser();
    if (userLogged == null) {
      return "redirect:/login";
    }
    Message msg = messageService.getMessageById(id);
    msg.setOpened(0);
    messageService.autoreply(msg);
    return "redirect:/messages";
  }
}
