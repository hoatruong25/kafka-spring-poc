package com.poc.common.persistence.repository;

import com.poc.common.persistence.model.MessageSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageSendRepository extends JpaRepository<MessageSend, Long> {
}


