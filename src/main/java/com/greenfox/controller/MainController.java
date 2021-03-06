package com.greenfox.controller;

import com.greenfox.model.*;
import com.greenfox.service.Broadcast;
import com.greenfox.repository.MessageRepository;
import com.greenfox.repository.UserRepository;
import com.greenfox.service.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {
  private String errorTextOnWebPage;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final Broadcast broadcast;

  @Autowired
  public MainController(UserRepository userRepository, MessageRepository messageRepository, Broadcast broadcast) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
    this.broadcast = broadcast;
  }

  @RequestMapping("/")
  public String main(Model model, HttpServletRequest request) {
    Logger.showLogWithOutParameter(request);
    if (userRepository.count() == 0) {
      return "redirect:/enter";
    } else {
      model.addAttribute("error", errorTextOnWebPage);
      model.addAttribute("userName", userRepository.findOne((long) 1).getName());
      model.addAttribute("messageList", messageRepository.findAll());
      return "index";
    }
  }

  @RequestMapping("/update")
  public String update(HttpServletRequest request, @RequestParam("newName") String newName) {
    Logger.showLog(request, "newname=" + newName);
    if (newName.isEmpty()) {
      errorTextOnWebPage = "The username field is empty.";
      return "redirect:/";
    } else {
      User user = userRepository.findOne((long) 1);
      updateExecute(request, user, newName);
      errorTextOnWebPage = "";
      return "redirect:/";
    }
  }

  @PutMapping("/update/execute")
  public void updateExecute(HttpServletRequest request, User user, String newName) {
    Logger.showLog(request, "newname=" + newName);
    user.setName(newName);
    userRepository.save(user);
  }

  @PostMapping("/send")
  public String send(HttpServletRequest request, @RequestParam("message") String message) {
    Logger.showLog(request, "message=" + message);
    ChatMessage chatMessage = new ChatMessage(userRepository.findOne((long) 1).getName(), message);
    messageRepository.save(chatMessage);
    broadcast.broadcastMessage(chatMessage);
    return "redirect:/";
  }
}
