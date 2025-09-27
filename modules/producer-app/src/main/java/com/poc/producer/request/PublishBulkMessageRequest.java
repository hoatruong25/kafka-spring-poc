package com.poc.producer.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PublishBulkMessageRequest {
    private int count;
}
