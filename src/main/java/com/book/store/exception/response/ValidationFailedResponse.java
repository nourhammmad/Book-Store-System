package com.book.store.exception.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
public class ValidationFailedResponse {

    @Builder.Default
    private final boolean success = false;
    @Builder.Default
    private final List<ViolationErrors> violations = new ArrayList<>();
    private final LocalDateTime timeStamp;
    private final HttpStatus httpStatus;
}