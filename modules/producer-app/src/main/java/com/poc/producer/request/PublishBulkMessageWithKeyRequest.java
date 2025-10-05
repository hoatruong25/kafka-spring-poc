package com.poc.producer.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PublishBulkMessageWithKeyRequest {
    private Integer count;
    private Boolean isErrorMessage;
}
