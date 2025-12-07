package com.example.Order_Photo.repository;

import com.example.Order_Photo.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findAllByOrderByTimestampDesc(Pageable pageable);

    long count();

    @Query("SELECT cm FROM ChatMessage cm LEFT JOIN FETCH cm.sender ORDER BY cm.timestamp ASC")
    Page<ChatMessage> findAllWithSenders(Pageable pageable);

}