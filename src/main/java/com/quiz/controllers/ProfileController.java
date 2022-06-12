package com.quiz.controllers;

import com.quiz.dao.GameDao;
import com.quiz.dto.GameDto;
import com.quiz.dto.QuizDto;
import com.quiz.entities.*;
import com.quiz.service.PaginationService;
import com.quiz.entities.Quiz;
import com.quiz.entities.User;
import com.quiz.service.QuizService;
import com.quiz.service.StoreFileService;
import com.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@CrossOrigin
public class ProfileController {

    private final StoreFileService storeFileService;
    private final UserService userRepo;
    private final QuizService quizService;
    private final PaginationService paginationService;
    private final GameDao gameDao;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserProfile(@PathVariable int userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userRepo.findProfileInfoByUserId(userId));
    }


    @GetMapping("/role-status/{role}/{status}")
    public ResponseEntity<ResponcePaginatedList<User>> getUsersByRoleStatus(@PathVariable String role,
                                                                            @PathVariable String status,
                                                                            @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                            @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<User> users = userRepo.findUsersByRoleStatus(role, status);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(users, limit, offset), users.size()));
    }

    @PatchMapping
    public ResponseEntity<User> updateUserProfile(@RequestBody User user) {
        boolean isRecordAffected = userRepo.updateUser(user);

        if (isRecordAffected) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PatchMapping("/password/{userId}")
    public ResponseEntity<String> updatePassword(@RequestBody String newPassword, @PathVariable int userId) {
        boolean isRecordAffected = userRepo.updatePasswordById(userId, newPassword);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/favorite/{userId}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getFavoriteQuizzes(@PathVariable int userId,
                                                                             @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                             @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<QuizDto> quizzes = quizService.findFavoriteQuizzes(userId);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }

    @GetMapping("/favorite/{userFilter}/{userId}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getFavoriteQuizzes(@PathVariable String userFilter,
                                                                             @PathVariable int userId,
                                                                             @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                             @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<QuizDto> quizzes = quizService.searchInFavoriteQuizzes(userId, userFilter);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }

    @PostMapping("/icon/{userId}")
    public ResponseEntity<String> changeProfileIcon(@RequestParam(value = "image") MultipartFile image, @PathVariable int userId) {
        boolean isRecordAffected = userRepo.updateProfileImage(storeFileService.uploadToLocalFileSystem(image), userId);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/image/{userId}")
    public ResponseEntity<ResponseText> getUserImage(@PathVariable int userId) {
        return ResponseEntity.ok(new ResponseText(userRepo.getImageByUserId(userId)));
    }

    @PostMapping("/status/{userId}")
    public ResponseEntity<String> updateNotificationStatus(@RequestBody String status, @PathVariable int userId) {
        boolean isRecordAffected = userRepo.changeNotificationStatus(status, userId);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<NotificationStatus> getUserNotificationStatus(@PathVariable int userId) {
        return ResponseEntity.ok(userRepo.getNotificationStatus(userId));
    }

    @DeleteMapping("/{id}")
    void deleteUserById(@PathVariable int id) {
        userRepo.deleteUserById(id);
    }

    @PatchMapping("/status/{userId}")
    public ResponseEntity<String> updateStatus(@PathVariable int userId) {
        boolean isRecordAffected = userRepo.updateStatusById(userId);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/played/{userId}")
    public ResponcePaginatedList<GameDto> getPlayedGames(@PathVariable int userId,
                                                         @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                         @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
                                                         @RequestParam(defaultValue = "", required = false, value = "sort") String sort,
                                                         @RequestParam(defaultValue = "", required = false, value = "search") String search) {
        if (search.isEmpty()) {
            return new ResponcePaginatedList<>(gameDao.getPlayedGame(userId, limit, offset, sort), gameDao.getNumberOfRecord(userId));
        }
        return new ResponcePaginatedList<>(gameDao.getFilteredPlayedGame(userId, limit, offset, sort, search), gameDao.getNumberOfRecord(userId));
    }

    @GetMapping("/game-result/{gameId}")
    public ResponseEntity<Set<Player>> getGameResult(@PathVariable int gameId) {
        return ResponseEntity.ok(gameDao.getGameResult(gameId));
    }

    @GetMapping("/reject/{userId}")
    public ResponseEntity<ResponcePaginatedList<Quiz>> getUserRejectedQuizzes(@PathVariable int userId,
                                                                              @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                              @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
                                                                              @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<Quiz> quizzes = quizService.getRejectedQuizzesByUserId(userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }

    @GetMapping("/reject-message/{quizId}")
    public ResponseEntity<List<RejectMessage>> getRejectMessage(@PathVariable int quizId) {
        return ResponseEntity.ok(quizService.getRejectMessages(quizId));
    }


}
