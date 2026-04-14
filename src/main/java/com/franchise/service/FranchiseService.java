package com.franchise.service;

import com.franchise.dto.request.FranchiseRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.dto.response.FranchiseResponse;
import com.franchise.model.Franchise;
import com.franchise.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;

    public Mono<FranchiseResponse> addFranchise(FranchiseRequest request) {
        return Mono.fromCallable(() -> {
            Franchise franchise = new Franchise();
            franchise.setName(request.getName());
            franchise.setBranchOffices(new ArrayList<>());
            Franchise saved = franchiseRepository.save(franchise);
            return new FranchiseResponse(saved.getId(), saved.getName());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<FranchiseResponse> updateFranchiseName(Long id, UpdateNameRequest request) {
        return Mono.fromCallable(() -> {
            Franchise franchise = franchiseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Franchise not found: " + id));
            franchise.setName(request.getName());
            Franchise saved = franchiseRepository.save(franchise);
            return new FranchiseResponse(saved.getId(), saved.getName());
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
