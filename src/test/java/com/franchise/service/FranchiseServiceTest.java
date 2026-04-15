package com.franchise.service;

import com.franchise.dto.request.FranchiseRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.dto.response.FranchiseResponse;
import com.franchise.model.Franchise;
import com.franchise.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FranchiseServiceTest {
    @Mock
    private FranchiseRepository franchiseRepository;
    @InjectMocks
    private FranchiseService franchiseService;

    private Franchise franchise;

    @BeforeEach
    void setUp(){
        franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Medellin");
        franchise.setBranchOffices(new ArrayList<>());
    }
    @Test
    @DisplayName("\"Should create a franchise and return its response")
    void addFranchise_success(){
        FranchiseRequest request = new FranchiseRequest();
        request.setName("Medellin");
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(franchise);

        StepVerifier.create(franchiseService.addFranchise(request))
                .expectNextMatches(response ->
                        response.getId().equals(1L) &&
                        response.getName().equals("Medellin")
                        )
                        .verifyComplete();
         verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should update franchise name successfully")
    void updateFranchiseName_success() {
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Rionegro");

        Franchise updated = new Franchise();
        updated.setId(1L);
        updated.setName("Rionegro");
        updated.setBranchOffices(new ArrayList<>());

        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(updated);

        StepVerifier.create(franchiseService.updateFranchiseName(1L, request))
                .expectNextMatches(response ->
                        response.getId().equals(1L) &&
                                response.getName().equals("Rionegro")
                )
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(1L);
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should return error when franchise not found on update")
    void updateFranchiseName_notFound() {
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Rionegro");

        when(franchiseRepository.findById(99L)).thenReturn(Optional.empty());

        StepVerifier.create(franchiseService.updateFranchiseName(99L, request))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().contains("Franchise not found: 99")
                )
                .verify();

        verify(franchiseRepository, times(1)).findById(99L);
        verify(franchiseRepository, never()).save(any());
    }

}
