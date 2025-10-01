package com.poc.common.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "messageError")
public class MessageError {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String topic;

    @Column
    private Integer partition;

    @Column
    private Long offsetNumber;

    @Column
    private String consumerName;

    @Column
    private String content;

    @Column
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
