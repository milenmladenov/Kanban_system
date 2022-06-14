package com.tusofia.diplomna.service.message;

import com.tusofia.diplomna.dto.SendMessageDto;
import com.tusofia.diplomna.model.Bug;
import com.tusofia.diplomna.model.Message;
import com.tusofia.diplomna.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface MessageService {
    List<Message> getMessagesByUser(User user);
    Message getMessageById(Long id);
    void deleteMessageById(Long id);
    Message save(SendMessageDto sendMessageDto, Long id);
    List<Message> getAllByReceiverAndOpenedIsFalse(User user);
    List<Message> findByReceiverAndOpenedIs(User user, int read);
    Message bugNotifyMessage(User sender, User receiver, Bug bug);
    void updateAttributes(User user, HttpServletRequest req);
    Message autoreply(Message message);
}
