package com.franchise.service;

import com.franchise.dto.request.ProductRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.dto.request.UpdateStockRequest;
import com.franchise.dto.response.ProductResponse;
import com.franchise.dto.response.TopStockProductResponse;
import com.franchise.model.BranchOffice;
import com.franchise.model.Product;
import com.franchise.repository.BranchOfficeRepository;
import com.franchise.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BranchOfficeRepository branchOfficeRepository;

    public Mono<ProductResponse> addProduct(Long branchOfficeId, ProductRequest request) {
        return Mono.fromCallable(() -> {
            BranchOffice branch = branchOfficeRepository.findById(branchOfficeId)
                    .orElseThrow(() -> new RuntimeException("Branch office not found: " + branchOfficeId));
            Product product = new Product();
            product.setName(request.getName());
            product.setStock(request.getStock());
            product.setBranchOffice(branch);
            Product saved = productRepository.save(product);
            return new ProductResponse(saved.getId(), saved.getName(),
                    saved.getStock(), branchOfficeId);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteProduct(Long productId) {
        return Mono.fromCallable(() -> {
            productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
            productRepository.deleteById(productId);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<ProductResponse> updateStock(Long productId, UpdateStockRequest request) {
        return Mono.fromCallable(() -> {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
            product.setStock(request.getStock());
            Product saved = productRepository.save(product);
            return new ProductResponse(saved.getId(), saved.getName(),
                    saved.getStock(), saved.getBranchOffice().getId());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ProductResponse> updateProductName(Long productId, UpdateNameRequest request) {
        return Mono.fromCallable(() -> {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
            product.setName(request.getName());
            Product saved = productRepository.save(product);
            return new ProductResponse(saved.getId(), saved.getName(),
                    saved.getStock(), saved.getBranchOffice().getId());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<TopStockProductResponse> getTopStockPerBranch(Long franchiseId) {
        return Mono.fromCallable(() ->
                        branchOfficeRepository.findByFranchiseId(franchiseId)
                ).subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(branches -> Flux.fromIterable(branches)
                        .flatMap(branch ->
                                Mono.fromCallable(() ->
                                                productRepository.findTopStockByBranchOfficeId(branch.getId())
                                        ).subscribeOn(Schedulers.boundedElastic())
                                        .flatMap(optProduct -> optProduct
                                                .map(product -> Mono.just(new TopStockProductResponse(
                                                        product.getId(),
                                                        product.getName(),
                                                        product.getStock(),
                                                        branch.getId(),
                                                        branch.getName()
                                                )))
                                                .orElse(Mono.empty())
                                        )
                        )
                );
    }
}