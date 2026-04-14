package com.franchise.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopStockProductResponse {
    private Long productId;
    private String productName;
    private Integer stock;
    private Long branchOfficeId;
    private String branchOfficeName;
}