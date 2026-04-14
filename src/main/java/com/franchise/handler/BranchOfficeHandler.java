package com.franchise.handler;
import com.franchise.dto.request.BranchOfficeRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.service.BranchOfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchOfficeHandler {

    private final BranchOfficeService branchOfficeService;

    public Mono<ServerResponse> addBranchOffice(ServerRequest request) {
        Long franchiseId = Long.valueOf(request.pathVariable("franchiseId"));
        return request.bodyToMono(BranchOfficeRequest.class)
                .flatMap(req -> branchOfficeService.addBranchOffice(franchiseId, req))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> updateBranchOfficeName(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(req -> branchOfficeService.updateBranchOfficeName(id, req))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
}