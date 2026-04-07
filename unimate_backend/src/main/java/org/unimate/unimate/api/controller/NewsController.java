package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.news.request.NewsPieceRequest;
import org.unimate.unimate.api.dto.news.response.NewsPieceResponse;
import org.unimate.unimate.domain.entities.NewsPiece;
import org.unimate.unimate.repository.NewsPieceRepository;
import org.unimate.unimate.service.NewsService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NewsController {

    NewsService newsService;
    NewsPieceRepository newsPieceRepository;

    @PostMapping
    public NewsPieceResponse create(@RequestBody NewsPieceRequest request) {
        return newsService.create(request);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public List<NewsPieceResponse> list() {
        return newsService.findAll();
    }

    @GetMapping("/latest")
    @PreAuthorize("permitAll()")
    public List<NewsPieceResponse> listLatest(@RequestParam(defaultValue = "10") int limit) {
        return newsService.findLatest(limit);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public NewsPieceResponse getById(@PathVariable Integer id) {
        return newsService.findById(id);
    }

    @PutMapping("/{id}")
    public NewsPieceResponse update(@PathVariable Integer id, @RequestBody NewsPieceRequest request) {
        NewsPiece newsPiece = newsPieceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        return newsService.update(newsPiece, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        NewsPiece newsPiece = newsPieceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        newsService.delete(newsPiece);
    }
}
