package com.notes.portal.controller;

import com.notes.portal.model.User;
import com.notes.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    /* ── REGISTER ── */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank())
            return ResponseEntity.badRequest().body("Name is required.");
        if (user.getEmail() == null || user.getEmail().isBlank())
            return ResponseEntity.badRequest().body("Email is required.");
        if (user.getPassword() == null || user.getPassword().length() < 6)
            return ResponseEntity.badRequest().body("Password must be at least 6 characters.");
        if (userRepo.existsByEmail(user.getEmail().trim().toLowerCase()))
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered. Please login.");

        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setName(user.getName().trim());
        userRepo.save(user);
        return ResponseEntity.ok("User Registered Successfully");
    }

    /* ── LOGIN — returns "Login Success|ActualName" ── */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User req) {
        if (req.getEmail() == null || req.getEmail().isBlank())
            return ResponseEntity.badRequest().body("Email is required.");
        if (req.getPassword() == null || req.getPassword().isBlank())
            return ResponseEntity.badRequest().body("Password is required.");

        String emailLower = req.getEmail().trim().toLowerCase();
        Optional<User> opt = userRepo.findByEmail(emailLower);

        if (opt.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No account found with this email. Please register first.");

        User user = opt.get();

        if (!user.getPassword().equals(req.getPassword().trim()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect password. Please try again.");

        /* Return Login Success with actual name separated by | */
        return ResponseEntity.ok("Login Success|" + user.getName());
    }

    /* ── OPTIONS preflight ── */
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok().build();
    }
}
