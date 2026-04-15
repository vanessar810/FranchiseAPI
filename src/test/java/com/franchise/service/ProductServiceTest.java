package com.franchise.service;

import com.franchise.dto.request.ProductRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.dto.request.UpdateStockRequest;
import com.franchise.dto.response.TopStockProductResponse;
import com.franchise.model.BranchOffice;
import com.franchise.model.Franchise;
import com.franchise.model.Product;
import com.franchise.repository.BranchOfficeRepository;
import com.franchise.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchOfficeRepository branchOfficeRepository;

    @InjectMocks
    private ProductService productService;

    private Franchise franchise;
    private BranchOffice branchOffice;
    private Product product;

    @BeforeEach
    void setUp() {
        franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Medellin");

        branchOffice = new BranchOffice();
        branchOffice.setId(1L);
        branchOffice.setName("Centro");
        branchOffice.setFranchise(franchise);
        branchOffice.setProducts(new ArrayList<>());

        product = new Product();
        product.setId(1L);
        product.setName("Bank account");
        product.setStock(50);
        product.setBranchOffice(branchOffice);
    }

    @Test
    @DisplayName("Should add a product to an existing branch office")
    void addProduct_success() {
        ProductRequest request = new ProductRequest();
        request.setName("Bank account");
        request.setStock(50);

        when(branchOfficeRepository.findById(1L)).thenReturn(Optional.of(branchOffice));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        StepVerifier.create(productService.addProduct(1L, request))
                .expectNextMatches(response ->
                        response.getId().equals(1L) &&
                                response.getName().equals("Bank account") &&
                                response.getStock().equals(50) &&
                                response.getBranchOfficeId().equals(1L)
                )
                .verifyComplete();

        verify(branchOfficeRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should return error when branch office not found on add product")
    void addProduct_branchNotFound() {
        ProductRequest request = new ProductRequest();
        request.setName("Bank account");
        request.setStock(50);

        when(branchOfficeRepository.findById(99L)).thenReturn(Optional.empty());

        StepVerifier.create(productService.addProduct(99L, request))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().contains("Branch office not found: 99")
                )
                .verify();

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void deleteProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(1L);

        StepVerifier.create(productService.deleteProduct(1L))
                .verifyComplete();

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return error when product not found on delete")
    void deleteProduct_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        StepVerifier.create(productService.deleteProduct(99L))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().contains("Product not found: 99")
                )
                .verify();

        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should update product stock successfully")
    void updateStock_success() {
        UpdateStockRequest request = new UpdateStockRequest();
        request.setStock(100);

        Product updated = new Product();
        updated.setId(1L);
        updated.setName("Bank account");
        updated.setStock(100);
        updated.setBranchOffice(branchOffice);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        StepVerifier.create(productService.updateStock(1L, request))
                .expectNextMatches(response ->
                        response.getStock().equals(100)
                )
                .verifyComplete();

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update product name successfully")
    void updateProductName_success() {
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Credit card");

        Product updated = new Product();
        updated.setId(1L);
        updated.setName("Credit card");
        updated.setStock(50);
        updated.setBranchOffice(branchOffice);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        StepVerifier.create(productService.updateProductName(1L, request))
                .expectNextMatches(response ->
                        response.getName().equals("Credit card")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return top stock product per branch for a franchise")
    void getTopStockPerBranch_success() {
        BranchOffice branch2 = new BranchOffice();
        branch2.setId(2L);
        branch2.setName("Oriental");
        branch2.setFranchise(franchise);
        branch2.setProducts(new ArrayList<>());

        Product topProduct2 = new Product();
        topProduct2.setId(2L);
        topProduct2.setName("Credit");
        topProduct2.setStock(120);
        topProduct2.setBranchOffice(branch2);

        when(branchOfficeRepository.findByFranchiseId(1L))
                .thenReturn(List.of(branchOffice, branch2));
        when(productRepository.findTopStockByBranchOfficeId(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.findTopStockByBranchOfficeId(2L))
                .thenReturn(Optional.of(topProduct2));

        StepVerifier.create(productService.getTopStockPerBranch(1L))
                .expectNextMatches(r ->
                        r.getBranchOfficeId().equals(1L) &&
                                r.getProductName().equals("Bank account") &&
                                r.getStock().equals(50)
                )
                .expectNextMatches(r ->
                        r.getBranchOfficeId().equals(2L) &&
                                r.getProductName().equals("Credit") &&
                                r.getStock().equals(120)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should skip branches with no products on top stock query")
    void getTopStockPerBranch_emptyBranch() {
        BranchOffice emptyBranch = new BranchOffice();
        emptyBranch.setId(2L);
        emptyBranch.setName("Empty Branch");
        emptyBranch.setFranchise(franchise);
        emptyBranch.setProducts(new ArrayList<>());

        when(branchOfficeRepository.findByFranchiseId(1L))
                .thenReturn(List.of(branchOffice, emptyBranch));
        when(productRepository.findTopStockByBranchOfficeId(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.findTopStockByBranchOfficeId(2L))
                .thenReturn(Optional.empty());

        StepVerifier.create(productService.getTopStockPerBranch(1L))
                .expectNextCount(1)
                .verifyComplete();
    }
}