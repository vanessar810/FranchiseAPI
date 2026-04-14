package com.franchise.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchOfficeRequest {

    @NotBlank(message = "Branch office name is required")
    private String name;
}