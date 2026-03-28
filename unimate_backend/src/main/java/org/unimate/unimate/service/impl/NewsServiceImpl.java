package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.news.request.NewsPieceRequest;
import org.unimate.unimate.api.dto.news.response.NewsPieceResponse;
import org.unimate.unimate.domain.entities.NewsPiece;
import org.unimate.unimate.domain.entities.NewsPieceTranslation;
import org.unimate.unimate.repository.NewsPieceRepository;
import org.unimate.unimate.service.NewsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NewsServiceImpl implements NewsService {

    NewsPieceRepository newsPieceRepository;

    @Override
    public List<NewsPieceResponse> findAll() {
        return newsPieceRepository.findAll().stream()
                .map(NewsPieceResponse::fromEntity)
                .toList();
    }

    @Override
    public List<NewsPieceResponse> findLatest(int limit) {
        return newsPieceRepository.findLatest(limit).stream()
                .map(NewsPieceResponse::fromEntity)
                .toList();
    }

    @Override
    public NewsPieceResponse findById(Integer id) {
        return newsPieceRepository.findById(id)
                .map(NewsPieceResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("News not found"));
    }

    @Override
    @Transactional
    public NewsPieceResponse create(NewsPieceRequest request) {
        NewsPiece newsPiece = NewsPiece.builder()
                .publishDate(LocalDateTime.now())
                .translations(new ArrayList<>())
                .build();

        for (var translationRequest : request.getTranslations()) {
            NewsPieceTranslation translation = NewsPieceTranslation.builder()
                    .newsPiece(newsPiece)
                    .language(translationRequest.getLanguage())
                    .title(translationRequest.getTitle())
                    .body(translationRequest.getBody())
                    .build();
            newsPiece.getTranslations().add(translation);
        }

        NewsPiece saved = newsPieceRepository.save(newsPiece);
        return NewsPieceResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public NewsPieceResponse update(NewsPiece newsPiece, NewsPieceRequest request) {
        newsPiece.getTranslations().clear();

        for (var translationRequest : request.getTranslations()) {
            NewsPieceTranslation translation = NewsPieceTranslation.builder()
                    .newsPiece(newsPiece)
                    .language(translationRequest.getLanguage())
                    .title(translationRequest.getTitle())
                    .body(translationRequest.getBody())
                    .build();
            newsPiece.getTranslations().add(translation);
        }

        NewsPiece saved = newsPieceRepository.save(newsPiece);
        return NewsPieceResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public void delete(NewsPiece newsPiece) {
        newsPieceRepository.delete(newsPiece);
    }
}
