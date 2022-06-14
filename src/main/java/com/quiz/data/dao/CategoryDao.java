package com.quiz.data.dao;

import com.quiz.data.dao.mapper.CategoryMapper;
import com.quiz.data.entities.Category;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String RESOURCE = "category";

    private static final String CATEGORY_BY_NAME = "SELECT id, name FROM categories WHERE name=?";
    private static final String CATEGORY_BY_ID = "SELECT id, name FROM categories WHERE id = ?";
    private static final String CATEGORIES_ALL = "SELECT id, name FROM categories";

    public Category findByName(String name) {
        List<Category> categories;

        try {
            categories = jdbcTemplate.query(
                    CATEGORY_BY_NAME,
                    new Object[]{name},
                    new CategoryMapper()
                    );
        } catch (DataAccessException e) {
            throw DatabaseException.resourceSearchException(RESOURCE, "'name': " + name);
        }

        return categories.get(0);
    }

    public Category getCategoryByCategoryId(int categoryId) {
        List<Category> categories;

        try {
            categories = jdbcTemplate.query(
                    CATEGORY_BY_ID,
                    new Object[]{categoryId},
                    new CategoryMapper());
        } catch (DataAccessException e) {
            throw DatabaseException.resourceSearchException(RESOURCE, "'categoryId': " + categoryId);
        }

        return categories.get(0);
    }

    public List<Category> getAllCategories() {
        List<Category> categories;

        categories = jdbcTemplate.query(
                CATEGORIES_ALL,
                new CategoryMapper());

        return categories;
    }
}
