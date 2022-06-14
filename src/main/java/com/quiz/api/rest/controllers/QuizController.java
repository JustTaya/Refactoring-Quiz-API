package com.quiz.api.rest.controllers;


import com.quiz.data.dto.QuizDto;
import com.quiz.data.entities.Quiz;
import com.quiz.data.entities.ResponcePaginatedList;
import com.quiz.data.entities.ResponseText;
import com.quiz.data.entities.StatusType;
import com.quiz.service.PaginationService;
import com.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final PaginationService paginationService;

    @GetMapping("/{userFilter}/{userId}")
    public ResponseEntity<ResponcePaginatedList<Quiz>> getUserQuizzes(@PathVariable String userFilter,
                                                                      @PathVariable int userId,
                                                                      @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                      @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
                                                                      @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<Quiz> quizzes = quizService.filterQuizzesByUserId(userFilter, userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }

    @GetMapping("/owned/{userId}")
    public ResponseEntity<ResponcePaginatedList<Quiz>> getUserQuizzes(@PathVariable int userId,
                                                                      @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                      @RequestParam(required = false, defaultValue = "0", value = "offset") int offset,
                                                                      @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<Quiz> quizzes = quizService.findQuizzesCreatedByUserId(userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable int quizId) {
        return ResponseEntity.ok(quizService.findQuizById(quizId));
    }

    @GetMapping("/info/{quizId}")
    public ResponseEntity<QuizDto> getQuizInfo(@PathVariable int quizId) {
        return ResponseEntity.ok(quizService.getQuizInfo(quizId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getAllQuizzes(@PathVariable int userId,
                                                                        @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                        @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<QuizDto> quizzes = quizService.findAllQuizzes(limit, offset, userId);
        return ResponseEntity.ok(new ResponcePaginatedList<>(quizzes, quizService.getNumberOfRecord()));
    }

    @GetMapping("/category/{categoryId}/{userId}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getQuizzesByCategory(@PathVariable int categoryId,
                                                                               @PathVariable int userId,
                                                                               @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                               @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<QuizDto> quizzes = quizService.findQuizzesByCategory(categoryId, userId);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<Quiz>> getQuizzesByTag(@PathVariable int tagId) {
        return ResponseEntity.ok(quizService.findQuizzesByTag(tagId));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Quiz>> getQuizzesByName(@PathVariable String name) {
        return ResponseEntity.ok(quizService.findQuizzesByName(name));
    }

    @PostMapping
    public ResponseEntity<QuizDto> insert(@RequestBody QuizDto quiz) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.insertQuiz(quiz));
    }

    @GetMapping("/image/{quizId}")
    public ResponseEntity<ResponseText> getQuizImage(@PathVariable int quizId) {
        return ResponseEntity.ok(new ResponseText(quizService.getImageByQuizId(quizId)));
    }

    @GetMapping("/top")
    public ResponseEntity<List<Quiz>> getTopQuizzes(@RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findTopPopularQuizzes(limit));
    }

    @GetMapping("/top/{categoryId}")
    public ResponseEntity<List<Quiz>> getTopQuizzesByCategory(@PathVariable int categoryId, @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findTopPopularQuizzesByCategory(categoryId, limit));
    }

    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<Quiz>> getRecentQuizzes(@PathVariable int userId, @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findRecentGames(userId, limit));
    }

    @GetMapping("/filter/{searchByUser}/{userId}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getFilteredQuizzes(@PathVariable String userFilter,
                                                                             @PathVariable int userId,
                                                                             @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                             @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<QuizDto> quizzes = quizService.getQuizzesByFilter(userFilter, userId);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }

    @PostMapping("/mark/{quizId}/{userId}")
    public ResponseEntity<String> markQuizAsFavorite(@PathVariable int quizId, @PathVariable int userId) {
        boolean isRecordAffected = quizService.markQuizAsFavorite(quizId, userId);
        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/unmark/{quizId}/{userId}")
    public ResponseEntity<String> unmarkQuizAsFavorite(@PathVariable int quizId, @PathVariable int userId) {
        boolean isRecordAffected = quizService.unmarkQuizAsFavorite(quizId, userId);
        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Quiz>> getRecommendations(@PathVariable int userId,
                                                         @RequestParam(required = false, defaultValue = "10", value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findRecommendations(userId, limit));
    }

    @GetMapping("/recommendations/friends/{userId}")
    public ResponseEntity<List<Quiz>> getRecommendationsByFriends(@PathVariable int userId,
                                                                  @RequestParam(required = false, defaultValue = "10", value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findRecommendationsByFriends(userId, limit));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<QuizDto>> getPopularQuizzes(@RequestParam(required = false, defaultValue = "10", value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findPopularQuizzes(limit));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<QuizDto>> getQuizzesByStatus(@PathVariable StatusType status) {
        return ResponseEntity.ok(quizService.findQuizzesByStatus(status));
    }

    @GetMapping
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getQuizzesByStatus(@RequestParam(name = "status") StatusType status,
                                                                             @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                             @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<QuizDto> quizzes = quizService.findQuizzesByStatus(status);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }


    @GetMapping("/filter/{searchText}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getFilteredPendingQuizzes(@PathVariable String searchText,
                                                                                    @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                                    @RequestParam(required = false, defaultValue = "0", value = "offset") int offset) {
        List<QuizDto> quizzes = quizService.getPendingQuizByFilter(searchText);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }
}
