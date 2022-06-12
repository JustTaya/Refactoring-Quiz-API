package com.quiz.controllers;

import com.quiz.entities.ResponcePaginatedList;
import com.quiz.entities.User;
import com.quiz.service.FriendsService;
import com.quiz.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile/friends")
@RequiredArgsConstructor
@CrossOrigin
public class FriendsController {
    private final FriendsService userRepo;
    private final PaginationService paginationService;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponcePaginatedList<User>> getFriends(@PathVariable int userId,
                                                                  @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                  @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
                                                                  @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<User> friends = userRepo.findFriendByUserId(userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(friends, limit, offset), friends.size()));
    }

    @GetMapping("/{userFilter}/{userId}")
    public ResponseEntity<ResponcePaginatedList<User>> getFriends(@PathVariable String userFilter,
                                                                  @PathVariable int userId,
                                                                  @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                  @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
                                                                  @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<User> friends = userRepo.filterFriendByUserId(userFilter, userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(friends, limit, offset), friends.size()));
    }
}
