package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repo.UserRepo;

import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private UserRepo userRepo;


    @GetMapping
    public String index(@AuthenticationPrincipal User user, Model model) {
        if (user != null) {
            model.addAttribute("user", user.getUsername());
            return "index";
        }
        model.addAttribute("user", "anonymous");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PreAuthorize(value = "hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping("/foruser/{id}")
        public String lookUser(@PathVariable(value = "id") long id, Model model) {
        if (!userRepo.existsById(id)) {
            return "redirect:/foruser";
        }
        Optional<User> user = userRepo.findById(id);
        ArrayList<User> res = new ArrayList<>();
        user.ifPresent(res::add);
        model.addAttribute("userr", res);
        return "foruser";
    }

    @PreAuthorize(value = "hasAuthority('ADMIN')")
    @GetMapping("/foradmin")
    public String forAdmin(Model model) {
        Iterable<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "foradmin";
    }

    @GetMapping("/edit/{id}")
    public String lookEdit(@PathVariable(value = "id") long id, Model model) {
        if (!userRepo.existsById(id)) {
            return "redirect:/index";
        }
        Optional<User> user = userRepo.findById(id);
        ArrayList<User> res = new ArrayList<>();
        user.ifPresent(res::add);
        model.addAttribute("user", res);
        return "edit";
    }

    @PostMapping("/remove/{id}")
    public String blogPostUpdate(@PathVariable(value = "id") long id, Model model) {
        User user = userRepo.findById(id).orElseThrow();
        userRepo.delete(user);
        return "redirect:/foradmin";
    }

    @PostMapping("/edit/{id}")
    public String blogPostUpdate(@PathVariable(value = "id") long id, @RequestParam String username,@RequestParam String password, @RequestParam String email, @RequestParam String phone, Model model) {
        User user = userRepo.findById(id).orElseThrow();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        userRepo.save(user);
        return "foruser";
    }

}
