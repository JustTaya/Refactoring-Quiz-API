package com.quiz.api.rest.controllers;

import com.quiz.data.entities.User;
import com.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/users/rating")
@RequiredArgsConstructor
public class UserRatingController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<Integer> getRatingByUser(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getRatingByUser(userId));
    }

    @GetMapping("/range/{userId}")
    public ResponseEntity<List<User>> getRatingByUser(@PathVariable int userId, @RequestParam(value = "range") int range) {
        return ResponseEntity.ok(userService.getRatingInRange(userId, range));
    }

    @GetMapping
    public ResponseEntity<List<User>> getRating(@RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                @RequestParam(value = "to", required = false, defaultValue = "10") int to) {
        return ResponseEntity.ok(userService.getRating(from, to));
    }
}
