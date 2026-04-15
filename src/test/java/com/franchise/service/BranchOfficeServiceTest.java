package com.franchise.service;

import com.franchise.dto.request.BranchOfficeRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.model.BranchOffice;
import com.franchise.model.Franchise;
import com.franchise.repository.BranchOfficeRepository;
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
public class BranchOfficeServiceTest {
    @Mock
    private BranchOfficeRepository branchOfficeRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private BranchOfficeService branchOfficeService;

    private Franchise franchise;
    private BranchOffice branchOffice;

    @BeforeEach
    void setUp() {
        franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Medellin");
        franchise.setBranchOffices(new ArrayList<>());

        branchOffice = new BranchOffice();
        branchOffice.setId(1L);
        branchOffice.setName("Centro");
        branchOffice.setFranchise(franchise);
        branchOffice.setProducts(new ArrayList<>());
    }

    @Test
    @DisplayName("Should add a branch office to an existing franchise")
    void addBranchOffice_success() {
        BranchOfficeRequest request = new BranchOfficeRequest();
        request.setName("Centro");

        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));
        when(branchOfficeRepository.save(any(BranchOffice.class))).thenReturn(branchOffice);

        StepVerifier.create(branchOfficeService.addBranchOffice(1L, request))
                .expectNextMatches(response ->
                        response.getId().equals(1L) &&
                                response.getName().equals("Centro") &&
                                response.getFranchiseId().equals(1L)
                )
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(1L);
        verify(branchOfficeRepository, times(1)).save(any(BranchOffice.class));
    }

    @Test
    @DisplayName("Should return error when franchise not found on add branch")
    void addBranchOffice_franchiseNotFound() {
        BranchOfficeRequest request = new BranchOfficeRequest();
        request.setName("Centro");

        when(franchiseRepository.findById(99L)).thenReturn(Optional.empty());

        StepVerifier.create(branchOfficeService.addBranchOffice(99L, request))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().contains("Franchise not found: 99")
                )
                .verify();

        verify(branchOfficeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update branch office name successfully")
    void updateBranchOfficeName_success() {
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Oriental");

        BranchOffice updated = new BranchOffice();
        updated.setId(1L);
        updated.setName("Oriental");
        updated.setFranchise(franchise);
        updated.setProducts(new ArrayList<>());

        when(branchOfficeRepository.findById(1L)).thenReturn(Optional.of(branchOffice));
        when(branchOfficeRepository.save(any(BranchOffice.class))).thenReturn(updated);

        StepVerifier.create(branchOfficeService.updateBranchOfficeName(1L, request))
                .expectNextMatches(response ->
                        response.getName().equals("Oriental")
                )
                .verifyComplete();

        verify(branchOfficeRepository, times(1)).findById(1L);
        verify(branchOfficeRepository, times(1)).save(any(BranchOffice.class));
    }

    @Test
    @DisplayName("Should return error when branch office not found on update")
    void updateBranchOfficeName_notFound() {
        UpdateNameRequest request = new UpdateNameRequest();
        request.setName("Oriental");

        when(branchOfficeRepository.findById(99L)).thenReturn(Optional.empty());

        StepVerifier.create(branchOfficeService.updateBranchOfficeName(99L, request))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().contains("Branch office not found: 99")
                )
                .verify();

        verify(branchOfficeRepository, never()).save(any());
    }
}

