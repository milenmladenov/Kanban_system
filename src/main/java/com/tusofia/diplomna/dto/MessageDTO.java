package com.tusofia.diplomna.dto;

import com.tusofia.diplomna.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

  private String subject;

  private String messageText;

  private User sender;

  private User receiver;
}
