package com.tusofia.diplomna.service.message;

import com.tusofia.diplomna.dto.MessageDTO;
import com.tusofia.diplomna.model.Message;
import com.tusofia.diplomna.model.User;
import com.tusofia.diplomna.repository.MessageRepository;
import com.tusofia.diplomna.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

  private MessageRepository messageRepository;
  private UserService userService;

  @Autowired
  @Lazy
  public MessageServiceImpl(MessageRepository messageRepository, UserService userService) {
    this.messageRepository = messageRepository;
    this.userService = userService;
  }

  @Override
  public List<Message> getMessagesByUser(User user) {
    return messageRepository.getAllByReceiver(user);
  }

  @Override
  public Message getMessageById(Long id) {
    return messageRepository.getMessageById(id);
  }

  @Override
  public void deleteMessageById(Long id) {
    messageRepository.delete(messageRepository.getMessageById(id));
  }

  @Override
  public Message autoreply(Message message) {
    return messageRepository.save(message);
  }

  @Override
  public Message save(MessageDTO messageDto, Long id) {
    User sender =
        userService.findByUser(SecurityContextHolder.getContext().getAuthentication().getName());
    Message message = new Message();
    message.setSender(sender);
    message.setReceiver(messageDto.getReceiver());
    message.setSubject(messageDto.getSubject());
    message.setMessageText(messageDto.getMessageText());
    return messageRepository.save(message);
  }

  @Override
  public List<Message> getAllByReceiverAndOpenedIsFalse(User user) {
    return messageRepository.getAllByReceiverAndOpenedIsFalse(user);
  }

  @Override
  public List<Message> findByReceiverAndOpenedIs(User user, int read) {
    return messageRepository.findByReceiverAndOpenedIs(user, read);
  }


  @Override
  public void updateAttributes(User user, HttpServletRequest req) {
    List<Message> unreadMessages = findByReceiverAndOpenedIs(user, 0);
    if (unreadMessages.size() > 0) {
      Message lastMessage = unreadMessages.get(unreadMessages.size() - 1);
      req.getSession().setAttribute("lastMessage", lastMessage);
    }
    req.getSession().setAttribute("unreadMessages", unreadMessages.size());
  }
}
