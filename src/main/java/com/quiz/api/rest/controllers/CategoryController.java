package com.quiz.api.rest.controllers;

import com.quiz.data.entities.Category;
import com.quiz.data.entities.ResponseText;
import com.quiz.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public ResponseEntity<ResponseText> getCategoryNameByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(new ResponseText(categoryService.findById(categoryId).getName()));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        Category category = categoryService.findByName(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(category);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categories);
    }
}
