package com.tusofia.diplomna.service.message;

import com.tusofia.diplomna.dto.MessageDTO;
import com.tusofia.diplomna.model.Message;
import com.tusofia.diplomna.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface MessageService {
  List<Message> getMessagesByUser(User user);

  Message getMessageById(Long id);

  void deleteMessageById(Long id);

  Message save(MessageDTO messageDto, Long id);

  List<Message> getAllByReceiverAndOpenedIsFalse(User user);

  List<Message> findByReceiverAndOpenedIs(User user, int read);

  void updateAttributes(User user, HttpServletRequest req);

  Message autoreply(Message message);
}
