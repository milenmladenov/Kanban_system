package com.tusofia.diplomna.controller.social;

import com.tusofia.diplomna.dto.SendMessageDto;
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
    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    public User getLoggedUser() {
        return userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/messages")
    public String getInbox(HttpServletRequest req, Model model) {
        User userLogged = getLoggedUser();
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
    public String openMessage(HttpServletRequest req, Model model,@RequestParam Long id,SendMessageDto sendMessageDto) {
        User userLogged = getLoggedUser();
        List<Message> unreadMessages = messageService.findByReceiverAndOpenedIs(userLogged, 0);
        // Get username and messages
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
            if (unreadMessages.size() >= 1) {
                req.getSession().setAttribute("unreadMessages", unreadMessages.size()-1);
            }
        }
        if (id != null) {
            Message msg = messageService.getMessageById(id);
            model.addAttribute("message", msg);
            msg.setOpened(1);
            messageService.save(sendMessageDto,userLogged.getId());
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
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
            model.addAttribute("message", new Message());
        }
        return "message-new";
    }

    @PostMapping("/message-new")
    public String sendMessage(Model model, Message message, Authentication authentication, SendMessageDto sendMessageDto) {
        User userLogged = getLoggedUser();
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
        }
        User receiver = userService.findByUser(message.getReceiver().getUsername());
        if (message.getReceiver().getId() != userLogged.getId()) {
            if (receiver != null) {
                if (!(message.getSubject().isEmpty()) && !(message.getMessageText().isEmpty())) {
                    messageService.save(sendMessageDto,userLogged.getId());
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


    @GetMapping("/message-to")
    public String reply(Model model, Authentication authentication, Message message, @RequestParam Long id) {
        User userLogged = getLoggedUser();
        User recepient = userService.getById(id);
        if (recepient != null) {
            if (userLogged.getId() != id) {
                model.addAttribute("recepient",recepient);
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

    @PostMapping("/message-to")
    public String replyPost(Model model, @RequestParam Long id,SendMessageDto sendMessageDto) {
        User userLogged = getLoggedUser();
        if (userLogged != null) {
            model.addAttribute("loggedUser", userLogged);
        }
        User target = userService.getById(id);
        if (target != null) {
            messageService.save(sendMessageDto,target.getId());
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
        Message msg = messageService.getMessageById(id);
        msg.setOpened(0);
        messageService.autoreply(msg);
        return "redirect:/messages";
    }
}