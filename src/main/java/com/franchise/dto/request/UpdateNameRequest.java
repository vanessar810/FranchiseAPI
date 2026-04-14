package com.franchise.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNameRequest {

    @NotBlank(message = "Name is required")
    private String name;
}