package com.poc.common.persistence.repository;

import com.poc.common.persistence.model.MessageReceived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReceivedRepository extends JpaRepository<MessageReceived, Long> {
}


