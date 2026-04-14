package com.franchise.handler;
import com.franchise.dto.request.ProductRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.dto.request.UpdateStockRequest;
import com.franchise.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductService productService;

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        Long branchOfficeId = Long.valueOf(request.pathVariable("branchOfficeId"));
        return request.bodyToMono(ProductRequest.class)
                .flatMap(req -> productService.addProduct(branchOfficeId, req))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        Long productId = Long.valueOf(request.pathVariable("productId"));
        return productService.deleteProduct(productId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateStock(ServerRequest request) {
        Long productId = Long.valueOf(request.pathVariable("productId"));
        return request.bodyToMono(UpdateStockRequest.class)
                .flatMap(req -> productService.updateStock(productId, req))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        Long productId = Long.valueOf(request.pathVariable("productId"));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(req -> productService.updateProductName(productId, req))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> getTopStockPerBranch(ServerRequest request) {
        Long franchiseId = Long.valueOf(request.pathVariable("franchiseId"));
        return ServerResponse.ok()
                .body(productService.getTopStockPerBranch(franchiseId),
                        com.franchise.dto.response.TopStockProductResponse.class);
    }
}