package com.online.eShopping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flywaydb.core.experimental.schemahistory.ResolvedSchemaHistoryItem;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO {

    private boolean success;
    private String message;
    private Object data;
    private LocalDateTime timestamp = LocalDateTime.now();



}
