package com.quiz.controllers;

import com.quiz.dto.ModeratorCommentDto;
import com.quiz.dto.QuizCheckDto;
import com.quiz.dto.QuizDto;
import com.quiz.entities.ModeratorComment;
import com.quiz.entities.ResponcePaginatedList;
import com.quiz.entities.StatusType;
import com.quiz.service.PaginationService;
import com.quiz.service.QuizCheckService;
import com.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderator")
@RequiredArgsConstructor
@CrossOrigin
public class ModeratorController {

    private final QuizService quizService;
    private final QuizCheckService quizCheckService;
    private final PaginationService paginationService;

    @PostMapping("/assignment/{quizId}")
    public ResponseEntity<String> assignModerator(@RequestBody String moderatorId, @PathVariable int quizId){
        boolean isRecordAffected = quizCheckService.assignModerator(quizId, Integer.parseInt(moderatorId));

        if (isRecordAffected){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/quizzes/{moderatorId}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getModeratorQuizzes(@PathVariable int moderatorId,
                                                                              @RequestParam(required = false, defaultValue = "10", value = "limit") int limit,
                                                                              @RequestParam(required = false, defaultValue = "0", value = "offset") int offset){
        List<QuizDto> quizzes = quizService.getModeratorQuizzes(moderatorId);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, limit, offset), quizzes.size()));
    }

    @GetMapping("/quizzes/check/{quizId}")
    public ResponseEntity<QuizCheckDto> getQuizCheck(@PathVariable int quizId) {
        return ResponseEntity.ok(quizCheckService.getQuizCheck(quizId));
    }

    @PatchMapping("/quizzes/status/{quizId}")
    public ResponseEntity<String> updateStatus(@RequestBody StatusType status, @PathVariable int quizId){
        boolean isRecordAffected = quizCheckService.updateStatusById(quizId, status);

        if (isRecordAffected){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/comments")
    public ResponseEntity<ModeratorComment> addComment(@RequestBody ModeratorComment comment){
        quizCheckService.updateStatusById(comment.getQuizId(), StatusType.DEACTIVATED);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizCheckService.addCommentByQuizId(comment));
    }

    @GetMapping("/comments/{quizId}")
    public ResponseEntity<List<ModeratorCommentDto>> getCommentHistory(@PathVariable int quizId){
        return ResponseEntity.ok(quizCheckService.getCommentHistory(quizId));
    }

    @DeleteMapping("/unassign/{quizId}")
    void unassignQuizById(@PathVariable int quizId) {
        quizService.unsignQuizById(quizId);
    }

    @DeleteMapping("/unassign-all/{moderatorId}")
    void unassignAllModeratorQuizById(@PathVariable int moderatorId) {
        quizService.unsignAllModeratorQuizById(moderatorId);
    }
}
