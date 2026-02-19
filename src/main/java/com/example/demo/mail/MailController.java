package com.example.demo.mail;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    // Admin sends internal mail
    @PostMapping("/admin/mails")
    public ResponseEntity<?> send(@RequestBody SendMailRequest req) {
        try {
            return ResponseEntity.ok(mailService.send(req));
        } catch (Exception e) {
            // ✅ show real error in frontend instead of generic 500
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // User inbox
    @GetMapping("/mails/inbox/{userId}")
    public List<MailResponse> inbox(@PathVariable Long userId) {
        return mailService.inbox(userId);
    }

    // Sent messages
    @GetMapping("/mails/sent/{userId}")
    public List<MailResponse> sent(@PathVariable Long userId) {
        return mailService.sent(userId);
    }

    @PatchMapping("/mails/{mailId}/read")
    public MailResponse read(@PathVariable Long mailId) {
        return mailService.markRead(mailId);
    }

    @DeleteMapping("/mails/{mailId}")
    public void delete(@PathVariable Long mailId) {
        mailService.delete(mailId);
    }
}
