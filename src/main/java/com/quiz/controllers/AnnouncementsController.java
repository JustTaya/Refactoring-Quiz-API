package com.quiz.controllers;

import com.quiz.dao.AnnouncementDao;
import com.quiz.entities.Announcement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/announce")
public class AnnouncementsController {

    @Autowired
    AnnouncementDao announcementDao;

    @GetMapping("/dash")
    public List<Announcement> getAnnouncement(@RequestParam(defaultValue = "0",required = false, value = "userId") int userId) {
        if (userId != 0) {
            return announcementDao.getAnnouncementsByUserId(userId);
        }
        return announcementDao.getAnnouncements();
    }
}
