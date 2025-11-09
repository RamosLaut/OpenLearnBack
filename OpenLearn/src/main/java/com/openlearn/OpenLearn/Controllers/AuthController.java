    package com.openlearn.OpenLearn.Controllers;

    import com.openlearn.OpenLearn.Model.MemberDTO;
    import com.openlearn.OpenLearn.Services.EmailService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.web.client.RestTemplate;

    import java.time.Instant;
    import java.time.temporal.ChronoUnit;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.Random;

    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {
        @Autowired
        private EmailService emailService;

        public record ForgotPasswordRequest(String email) {}

        @Autowired
        private RestTemplate restTemplate;

        @PostMapping("/forgot-password")
        public ResponseEntity<String> handleForgotPassword(@RequestBody ForgotPasswordRequest requestBody) {

            String email = requestBody.email();
            String jsonServerUrl = "http://localhost:3000/members?email=" + email;

            try {
                ResponseEntity<MemberDTO[]> response = restTemplate.getForEntity(jsonServerUrl, MemberDTO[].class);
                MemberDTO[] members = response.getBody();

                if (members != null && members.length > 0) {
                    MemberDTO member = members[0];

                    String resetCode = String.format("%06d", new Random().nextInt(999999));
                    Instant expiryTime = Instant.now().plus(1, ChronoUnit.HOURS);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("resetCode", resetCode);
                    updates.put("resetCodeExpiry", expiryTime.toString());

                    String updateUrl = "http://localhost:3000/members/" + member.getId();
                    restTemplate.patchForObject(updateUrl, updates, Map.class); //

                    boolean emailSent = emailService.sendPasswordResetEmail(email, resetCode);

                    if (emailSent) {
                        return ResponseEntity.ok("Reset code sent successfully.");
                    } else {
                        return ResponseEntity.status(500).body("Error sending email.");
                    }
                } else {
                    return ResponseEntity.ok("If email exists, code was sent.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
            }
        }

        public record ResetPasswordRequest(String email, String code, String newPassword) {}

        @PostMapping("/reset-password")
        public ResponseEntity<String> handleResetPassword(@RequestBody ResetPasswordRequest request) {
            String jsonServerUrl = "http://localhost:3000/members?email=" + request.email();
            try {
                ResponseEntity<MemberDTO[]> response = restTemplate.getForEntity(jsonServerUrl, MemberDTO[].class);
                MemberDTO[] members = response.getBody();
                if (members == null || members.length == 0) {
                    return ResponseEntity.badRequest().body("Invalid code or email.");
                }
                MemberDTO member = members[0];
                if (member.getResetCode() == null || !member.getResetCode().equals(request.code())) {
                    return ResponseEntity.badRequest().body("Invalid code.");
                }
                Instant expiryTime = Instant.parse(member.getResetCodeExpiry());
                if (Instant.now().isAfter(expiryTime)) {
                    return ResponseEntity.badRequest().body("Code has expired.");
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", request.newPassword());
                updates.put("resetCode", null);
                updates.put("resetCodeExpiry", null);

                String updateUrl = "http://localhost:3000/members/" + member.getId();
                restTemplate.patchForObject(updateUrl, updates, Map.class);

                return ResponseEntity.ok("Password reset successfully.");

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
            }
        }
    }
