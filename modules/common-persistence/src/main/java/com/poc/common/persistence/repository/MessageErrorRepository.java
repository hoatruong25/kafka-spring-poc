package com.poc.common.persistence.repository;

import com.poc.common.persistence.model.MessageError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageErrorRepository extends JpaRepository<MessageError, Long> {
}
