package com.example.Order_Photo.controller;

import com.example.Order_Photo.model.Service;
import com.example.Order_Photo.model.User;
import com.example.Order_Photo.repository.UserRepository;
import com.example.Order_Photo.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Service> services = serviceService.getAllActiveServices();
        model.addAttribute("services", services);
        return "index";
    }

    @GetMapping("/services")
    public String services() {
        return "services";
    }

    @GetMapping("/order")
    public String orderPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                model.addAttribute("userEmail", user.getEmail());
                model.addAttribute("userPhone", user.getPhoneNumber());
            }
        }
        return "order";
    }

    @GetMapping("/order/success")
    public String orderSuccess(@RequestParam Long id, Model model) {
        model.addAttribute("orderId", id);
        return "order-success";
    }

    @GetMapping("/prices")
    public String prices() {
        return "prices";
    }

    @GetMapping("/contacts")
    public String contacts() {
        return "contacts";
    }
}