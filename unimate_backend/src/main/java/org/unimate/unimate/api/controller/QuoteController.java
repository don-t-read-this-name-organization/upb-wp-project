package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.quote.request.QuoteRequest;
import org.unimate.unimate.api.dto.quote.response.QuoteResponse;
import org.unimate.unimate.domain.entities.Quote;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.service.QuoteService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class QuoteController {

    QuoteService quoteService;

    @PostMapping
    public QuoteResponse create(@RequestBody QuoteRequest request) {
        return quoteService.createPending(request);
    }

    @GetMapping
    public List<QuoteResponse> list() {
        return quoteService.findAll().stream().map(QuoteResponse::fromEntity).toList();
    }

    @GetMapping("/random")
    public List<QuoteResponse> getRandomQuotes(@RequestParam(defaultValue = "1") int number) {
        return quoteService.findNRandomQuotes(number).stream().map(QuoteResponse::fromEntity).toList();
    }

    @GetMapping("/pending")
    public List<QuoteResponse> getPendingQuotes() {
        return quoteService.findAllPending().stream().map(QuoteResponse::fromEntity).toList();
    }

    @PostMapping("/{id}/approve")
    public QuoteResponse approve(@PathVariable Integer id) {
        return quoteService.approve(id);
    }

    @PostMapping("/{id}/reject")
    public void reject(@PathVariable Integer id) {
        quoteService.reject(id);
    }

    @GetMapping("/{id}")
    public QuoteResponse getById(@PathVariable Integer id) {
        return quoteService.findAll().stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .map(QuoteResponse::fromEntity)
                .orElseThrow(() -> new NotFoundException("Quote", id));
    }

    @PutMapping("/{id}")
    public QuoteResponse update(@PathVariable Integer id, @RequestBody QuoteRequest request) {
        Quote quote = quoteService.findAll().stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Quote", id));
        return quoteService.update(quote, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        Quote quote = quoteService.findAll().stream()
                .filter(q -> q.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Quote", id));
        quoteService.delete(quote);
    }
}
