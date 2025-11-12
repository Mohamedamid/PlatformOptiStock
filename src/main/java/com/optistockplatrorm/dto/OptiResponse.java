package com.optistockplatrorm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptiResponse {
    private String message;
    private Object data;
    private int status;
}
