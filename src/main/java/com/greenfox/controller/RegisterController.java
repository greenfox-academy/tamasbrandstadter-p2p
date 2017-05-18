package com.greenfox.controller;

import com.greenfox.model.Log;
import com.greenfox.model.User;
import com.greenfox.service.ErrorMessage;
import com.greenfox.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {
  @Autowired
  private UserRepository userRepository;
  private String error;
  private static String env = System.getenv("CHAT_APP_LOGLEVEL");

  @RequestMapping("/enter")
  public String register(Model model) {
    Log log = new Log("/enter", "REQUEST", "");
    if (!env.equals("ERROR")) {
      log.setLogLevel(env);
      System.out.println(log.toString());
    }
    if (userRepository.count() > 0) {
      return "redirect:/";
    } else {
      model.addAttribute("error", error);
      return "enter";
    }
  }

  @PostMapping("/enter/add")
  public String addNewUser(Model model, @RequestParam("name") String name) {
    Log log = new Log("/enter/add", "POST", "name=" + name);
    if (!env.equals("ERROR")) {
      log.setLogLevel(env);
      System.out.println(log.toString());
    }
    if (name.isEmpty()) {
      error = "The username field is empty.";
      model.addAttribute("error", error);
      return "redirect:/enter";
    } else {
      userRepository.save(new User(name));
      error = "";
      model.addAttribute("error", error);
      return "redirect:/";
    }
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ErrorMessage showError(MissingServletRequestParameterException e) {
    Log log = new Log();
    log.setLogLevel("ERROR");
    log.setErrorMessage(e.getMessage());
    System.out.println(log.getErrorMessage());
    System.out.println(log.toString());
    return new ErrorMessage("Missing servlet parameter exception.");
  }
}
