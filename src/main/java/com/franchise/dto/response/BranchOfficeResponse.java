package com.franchise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BranchOfficeResponse {
    private Long id;
    private String name;
    private Long franchiseId;
}