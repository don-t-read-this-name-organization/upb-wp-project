package org.unimate.unimate.service;

import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.news.request.NewsPieceRequest;
import org.unimate.unimate.api.dto.news.response.NewsPieceResponse;
import org.unimate.unimate.domain.entities.NewsPiece;

import java.util.List;

public interface NewsService {
    List<NewsPieceResponse> findAll();
    List<NewsPieceResponse> findLatest(int limit);
    NewsPieceResponse findById(Integer id);
    
    @Transactional
    NewsPieceResponse create(NewsPieceRequest request);
    
    @Transactional
    NewsPieceResponse update(NewsPiece newsPiece, NewsPieceRequest request);
    
    @Transactional
    void delete(NewsPiece newsPiece);
}
