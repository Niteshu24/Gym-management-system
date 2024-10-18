package com.example.demo.service;

import com.example.demo.entity.Membership;
import com.example.demo.entity.User;
import com.example.demo.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipService {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private JavaMailSender emailSender; // To send emails

    // Method to get all memberships
    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

// Method to save membership
public void saveMembership(Membership membership) {
    membershipRepository.save(membership);
}

    public Membership getMembershipById(Long id) {
        return membershipRepository.findById(id).orElse(null); // or throw an exception if you prefer
    }

    // Method to create a membership and send an email
    public void createMembership(String name, String phoneNumber, String membershipPeriod, User user) {
        Membership membership = new Membership();
        membership.setName(name); // from the form
        membership.setPhoneNumber(phoneNumber); // from the form
        membership.setMembershipPeriod(membershipPeriod); // from the form
        membership.setUser(user); // link to the User entity

        // Save the membership using the repository
        membershipRepository.save(membership);

        // Send confirmation email to the user
        sendMembershipConfirmationEmail(user.getEmail(), name, membershipPeriod);
    }

    // Method to send email notification
    private void sendMembershipConfirmationEmail(String email, String name, String membershipPeriod) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Membership Activation Confirmation");
        message.setText("Hello " + name + ",\n\n"
                + "Your membership has been activated for " + membershipPeriod + ".\n"
                + "Thank you for choosing us!\n\n"
                + "Best regards,\n"
                + "Your Gym Management Team");

        // Send the email
        emailSender.send(message);
    }
}
