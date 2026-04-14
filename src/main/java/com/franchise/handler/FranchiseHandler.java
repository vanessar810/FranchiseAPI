package com.franchise.handler;
import com.franchise.dto.request.FranchiseRequest;
import com.franchise.dto.request.UpdateNameRequest;
import com.franchise.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final FranchiseService franchiseService;

    public Mono<ServerResponse> addFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(franchiseService::addFranchise)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(req -> franchiseService.updateFranchiseName(id, req))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
}