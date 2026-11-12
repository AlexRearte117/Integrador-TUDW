package com.taller.adapter.data.api;

import java.time.LocalDateTime;

public record Api_Error(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path) {
}
