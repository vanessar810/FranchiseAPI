package com.franchise.configuration;

import com.franchise.dto.request.*;
import com.franchise.dto.response.BranchOfficeResponse;
import com.franchise.dto.response.FranchiseResponse;
import com.franchise.dto.response.ProductResponse;
import com.franchise.dto.response.TopStockProductResponse;
import com.franchise.handler.BranchOfficeHandler;
import com.franchise.handler.FranchiseHandler;
import com.franchise.handler.ProductHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
@RequiredArgsConstructor
public class RouterConfig {

    private final FranchiseHandler franchiseHandler;
    private final BranchOfficeHandler branchOfficeHandler;
    private final ProductHandler productHandler;

    @Bean
    @RouterOperations({
            // Franchise operations
            @RouterOperation(
                    path = "/api/franchises",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "addFranchise",
                    operation = @Operation(
                            operationId = "addFranchise",
                            summary = "Create a new franchise",
                            tags = {"Franchise"},
                            requestBody = @RequestBody(
                                    description = "Franchise details",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Franchise created successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid input data"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/franchises/{id}/name",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Update franchise name",
                            tags = {"Franchise"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "Franchise ID",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "New franchise name",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Franchise name updated successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Franchise not found"
                                    )
                            }
                    )
            ),

            // Branch office operations
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches",
                    method = RequestMethod.POST,
                    beanClass = BranchOfficeHandler.class,
                    beanMethod = "addBranchOffice",
                    operation = @Operation(
                            operationId = "addBranchOffice",
                            summary = "Add a branch office to a franchise",
                            tags = {"Branch Office"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            description = "Franchise ID",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Branch office details",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BranchOfficeRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Branch office created successfully",
                                            content = @Content(schema = @Schema(implementation = BranchOfficeResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Franchise not found"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/branches/{id}/name",
                    method = RequestMethod.PATCH,
                    beanClass = BranchOfficeHandler.class,
                    beanMethod = "updateBranchOfficeName",
                    operation = @Operation(
                            operationId = "updateBranchOfficeName",
                            summary = "Update branch office name",
                            tags = {"Branch Office"},
                            parameters = {
                                    @Parameter(
                                            name = "id",
                                            description = "Branch office ID",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "New branch office name",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Branch office name updated successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch office not found"
                                    )
                            }
                    )
            ),

            // Product operations
            @RouterOperation(
                    path = "/api/branches/{branchOfficeId}/products",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "addProduct",
                    operation = @Operation(
                            operationId = "addProduct",
                            summary = "Add a product to a branch office",
                            tags = {"Product"},
                            parameters = {
                                    @Parameter(
                                            name = "branchOfficeId",
                                            description = "Branch office ID",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "Product details",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Product created successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Branch office not found"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete a product",
                            tags = {"Product"},
                            parameters = {
                                    @Parameter(
                                            name = "productId",
                                            description = "Product ID",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Product deleted successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Product not found"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/stock",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateStock",
                    operation = @Operation(
                            operationId = "updateProductStock",
                            summary = "Update product stock",
                            tags = {"Product"},
                            parameters = {
                                    @Parameter(
                                            name = "productId",
                                            description = "Product ID",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "New stock quantity",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateStockRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Stock updated successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Product not found"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/products/{productId}/name",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update product name",
                            tags = {"Product"},
                            parameters = {
                                    @Parameter(
                                            name = "productId",
                                            description = "Product ID",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    description = "New product name",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Product name updated successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Product not found"
                                    )
                            }
                    )
            ),

            // Reports
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/top-stock",
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getTopStockPerBranch",
                    operation = @Operation(
                            operationId = "getTopStockPerBranch",
                            summary = "Get product with highest stock per branch for a franchise",
                            tags = {"Reports"},
                            parameters = {
                                    @Parameter(
                                            name = "franchiseId",
                                            description = "Franchise ID",
                                            required = true,
                                            in = ParameterIn.PATH
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Top stock products retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = TopStockProductResponse.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Franchise not found"
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route()

                // Franchise routes
                .POST("/api/franchises", franchiseHandler::addFranchise)
                .PATCH("/api/franchises/{id}/name", franchiseHandler::updateFranchiseName)

                // Branch office routes
                .POST("/api/franchises/{franchiseId}/branches", branchOfficeHandler::addBranchOffice)
                .PATCH("/api/branches/{id}/name", branchOfficeHandler::updateBranchOfficeName)

                // Product routes
                .POST("/api/branches/{branchOfficeId}/products", productHandler::addProduct)
                .DELETE("/api/products/{productId}", productHandler::deleteProduct)
                .PATCH("/api/products/{productId}/stock", productHandler::updateStock)
                .PATCH("/api/products/{productId}/name", productHandler::updateProductName)

                // Top stock per branch for a franchise
                .GET("/api/franchises/{franchiseId}/top-stock", productHandler::getTopStockPerBranch)

                .build();
    }


}