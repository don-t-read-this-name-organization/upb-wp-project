package org.unimate.unimate.service;

import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.quote.request.QuoteRequest;
import org.unimate.unimate.api.dto.quote.response.QuoteResponse;
import org.unimate.unimate.domain.entities.Quote;

import java.util.List;

public interface QuoteService {
    List<Quote> findAll();
    List<Quote> findNRandomQuotes(int number);
    List<Quote> findAllPending();
    Quote save(Quote quote);
    @Transactional
    QuoteResponse create(QuoteRequest request);

    @Transactional
    QuoteResponse createPending(QuoteRequest request);

    @Transactional
    QuoteResponse update(Quote quote, QuoteRequest request);

    @Transactional
    QuoteResponse approve(Integer id);

    @Transactional
    void reject(Integer id);

    void delete(Quote quote);
}
