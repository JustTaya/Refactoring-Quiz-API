package com.quiz.service;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginationService {
    public <T> List<T> paginate(List<T> listForPagination, int limit, int offset){
        PagedListHolder<T> pageToReturn = new PagedListHolder<>(listForPagination);
        pageToReturn.setPage(offset);
        pageToReturn.setPageSize(limit);

        return pageToReturn.getPageList();
    }
}
