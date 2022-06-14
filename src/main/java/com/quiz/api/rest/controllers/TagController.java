package com.quiz.api.rest.controllers;

import com.quiz.data.entities.Tag;
import com.quiz.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/quizzes/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/{tagId}")
    public ResponseEntity<Tag> getTagById(@PathVariable int tagId) {
        return ResponseEntity.ok(tagService.findById(tagId));
    }

    @GetMapping("/name/{tagName}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String tagName) {
        return ResponseEntity.ok(tagService.findTagByName(tagName));
    }

    @GetMapping("/regex-name/{regexName}")
    public ResponseEntity<List<Tag>> getTagsByName(@PathVariable String regexName) {
        return ResponseEntity.ok(tagService.findTagsByName(regexName));

    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<Tag>> getTagsByQuiz(@PathVariable int quizId) {
        return ResponseEntity.ok(tagService.findTagsByQuiz(quizId));
    }
}
