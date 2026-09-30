package com.boatsafari.controller;

import com.boatsafari.model.Guide;
import com.boatsafari.repository.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
@CrossOrigin(origins = "*")
public class GuideController {

    @Autowired
    private GuideRepository guideRepository;

    @GetMapping
    public ResponseEntity<List<Guide>> getAllGuides() {
        return ResponseEntity.ok(guideRepository.findAll());
    }
}