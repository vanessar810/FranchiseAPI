package com.franchise.service;

import com.franchise.dto.request.BranchOfficeRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.dto.response.BranchOfficeResponse;
import com.franchise.model.BranchOffice;
import com.franchise.model.Franchise;
import com.franchise.repository.BranchOfficeRepository;
import com.franchise.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BranchOfficeService {

    private final BranchOfficeRepository branchOfficeRepository;
    private final FranchiseRepository franchiseRepository;

    public Mono<BranchOfficeResponse> addBranchOffice(Long franchiseId, BranchOfficeRequest request) {
        return Mono.fromCallable(() -> {
            Franchise franchise = franchiseRepository.findById(franchiseId)
                    .orElseThrow(() -> new RuntimeException("Franchise not found: " + franchiseId));
            BranchOffice branch = new BranchOffice();
            branch.setName(request.getName());
            branch.setFranchise(franchise);
            branch.setProducts(new ArrayList<>());
            BranchOffice saved = branchOfficeRepository.save(branch);
            return new BranchOfficeResponse(saved.getId(), saved.getName(), franchiseId);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<BranchOfficeResponse> updateBranchOfficeName(Long id, UpdateNameRequest request) {
        return Mono.fromCallable(() -> {
            BranchOffice branch = branchOfficeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Branch office not found: " + id));
            branch.setName(request.getName());
            BranchOffice saved = branchOfficeRepository.save(branch);
            return new BranchOfficeResponse(saved.getId(), saved.getName(),
                    saved.getFranchise().getId());
        }).subscribeOn(Schedulers.boundedElastic());
    }
}