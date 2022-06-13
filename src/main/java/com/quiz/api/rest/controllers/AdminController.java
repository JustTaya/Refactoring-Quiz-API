package com.quiz.api.rest.controllers;

import com.quiz.data.dto.UserDto;
import com.quiz.data.entities.ResponcePaginatedList;
import com.quiz.data.entities.User;
import com.quiz.service.AdminService;
import com.quiz.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {
    private final AdminService adminService;
    private final PaginationService paginationService;

    @PostMapping
    public ResponseEntity<UserDto> addAdminUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.addAdminUser(user));
    }

    @GetMapping
    public ResponseEntity<ResponcePaginatedList<User>> getAdminUsers(@RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                     @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<User> users = adminService.findAdminsUsers();
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(users, limit, offset), users.size()));
    }
}
