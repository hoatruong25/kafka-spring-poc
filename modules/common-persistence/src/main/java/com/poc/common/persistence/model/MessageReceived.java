package com.poc.common.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Setter
@Getter
@Entity
@Table(name = "MessageReceived")
public class MessageReceived {
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


