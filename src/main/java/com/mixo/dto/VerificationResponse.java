package com.mixo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerificationResponse {
    private String status;
    private String message;
    private Object data;
}
