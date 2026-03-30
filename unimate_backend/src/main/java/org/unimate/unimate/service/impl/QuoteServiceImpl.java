package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.unimate.unimate.api.dto.quote.request.QuoteRequest;
import org.unimate.unimate.api.dto.quote.response.QuoteResponse;
import org.unimate.unimate.domain.entities.Quote;
import org.unimate.unimate.repository.QuoteRepository;
import org.unimate.unimate.service.QuoteService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class QuoteServiceImpl implements QuoteService {
    QuoteRepository quoteRepository;

    @Override
    public List<Quote> findAll() {
        return quoteRepository.findAll();
    }

    @Override
    public List<Quote> findNRandomQuotes(int number) {
        return quoteRepository.findNRandomQuotes(number);
    }

    @Override
    public List<Quote> findAllPending() {
        return quoteRepository.findAllPending();
    }

    @Override
    public QuoteResponse create(QuoteRequest request) {
        Quote quote = Quote.builder()
                .text(request.getText())
                .author(request.getAuthor())
                .active(true)
                .build();
        Quote saved = quoteRepository.save(quote);
        return QuoteResponse.fromEntity(saved);
    }

    @Override
    public QuoteResponse createPending(QuoteRequest request) {
        Quote quote = Quote.builder()
                .text(request.getText())
                .author(request.getAuthor())
                .active(false)
                .build();
        Quote saved = quoteRepository.save(quote);
        return QuoteResponse.fromEntity(saved);
    }

    @Override
    public QuoteResponse update(Quote quote, QuoteRequest request) {
        quote.setText(request.getText());
        quote.setAuthor(request.getAuthor());
        Quote saved = quoteRepository.save(quote);
        return QuoteResponse.fromEntity(saved);
    }

    @Override
    public QuoteResponse approve(Integer id) {
        Quote quote = quoteRepository.findPendingById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found"));
        quote.setActive(true);
        Quote saved = quoteRepository.save(quote);
        return QuoteResponse.fromEntity(saved);
    }

    @Override
    public void reject(Integer id) {
        Quote quote = quoteRepository.findPendingById(id)
                .orElseThrow(() -> new RuntimeException("Quote not found"));
        quoteRepository.delete(quote);
    }

    @Override
    public void delete(Quote quote) {
        quoteRepository.delete(quote);
    }
}
