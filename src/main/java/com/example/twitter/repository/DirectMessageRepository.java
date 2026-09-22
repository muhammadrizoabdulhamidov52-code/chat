package com.example.twitter.repository;

import com.example.twitter.model.AppUser;
import com.example.twitter.model.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    @Query("SELECT m FROM DirectMessage m WHERE " +
            "(m.sender = :userA AND m.receiver = :userB) OR (m.sender = :userB AND m.receiver = :userA) " +
            "ORDER BY m.createdAt ASC")
    List<DirectMessage> findConversation(@Param("userA") AppUser userA, @Param("userB") AppUser userB);

    @Query("SELECT DISTINCT m.receiver FROM DirectMessage m WHERE m.sender = :user")
    List<AppUser> findReceiversFromUser(@Param("user") AppUser user);

    @Query("SELECT DISTINCT m.sender FROM DirectMessage m WHERE m.receiver = :user")
    List<AppUser> findSendersToUser(@Param("user") AppUser user);
}