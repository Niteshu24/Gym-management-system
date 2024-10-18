package com.example.demo.controller;

import com.example.demo.entity.Membership;
import com.example.demo.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    // Method to handle membership creation
    @PostMapping("/membership")
    public String processCreateMembership(@RequestParam("name") String name,
                                          @RequestParam("phoneNumber") String phoneNumber,
                                          @RequestParam("membershipPeriod") String membershipPeriod,
                                          Model model) {
        Membership newMembership = new Membership(name, phoneNumber, membershipPeriod, null);
        membershipService.saveMembership(newMembership);
        
        // Here you could add logic to send an email if desired
        // sendEmail(newMembership); // Implement this method if you want to send emails
        
        return "redirect:/membership"; // Redirect to a success page or membership list after creation
    }
}
