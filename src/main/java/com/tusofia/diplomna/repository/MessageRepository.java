package com.tusofia.diplomna.repository;

import com.tusofia.diplomna.model.Message;
import com.tusofia.diplomna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> getAllByReceiver(User user);

  Message getMessageById(Long id);

  void deleteById(Long id);

  List<Message> getAllByReceiverAndOpenedIsFalse(User user);

  List<Message> findByReceiverAndOpenedIs(User user, int read);
}
