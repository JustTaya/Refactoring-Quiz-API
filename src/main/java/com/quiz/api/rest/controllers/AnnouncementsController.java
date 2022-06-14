package com.quiz.api.rest.controllers;

import com.quiz.data.dao.AnnouncementDao;
import com.quiz.data.entities.Announcement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/announcements")
public class AnnouncementsController {

    private final AnnouncementDao announcementDao;

    @GetMapping
    public List<Announcement> getAnnouncement(@RequestParam(defaultValue = "0",required = false, value = "userId") int userId) {
        if (userId != 0) {
            return announcementDao.getAnnouncementsByUserId(userId);
        }
        return announcementDao.getAnnouncements();
    }
}
