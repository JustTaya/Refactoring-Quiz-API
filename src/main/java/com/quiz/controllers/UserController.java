package com.quiz.controllers;

import com.quiz.entities.ResponcePaginatedList;
import com.quiz.entities.User;
import com.quiz.service.PaginationService;
import com.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PaginationService paginationService;

    @GetMapping("/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping("/filter/{userFilter}")
    public ResponseEntity<ResponcePaginatedList<User>> getFilteredUsers(@PathVariable String userFilter,
                                                                        @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                        @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<User> users = userService.getUsersByFilter(userFilter);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(users, limit, offset), users.size()));
    }

    @GetMapping("/activate/{code}")
    public ResponseEntity<User> getByActivationCode(@PathVariable String code) {
        return ResponseEntity.ok(userService.findByActivationCode(code));
    }
}
