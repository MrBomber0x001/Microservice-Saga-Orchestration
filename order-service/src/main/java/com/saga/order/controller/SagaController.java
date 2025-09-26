package com.saga.order.controller;

import com.saga.core.model.SagaTransaction;
import com.saga.order.service.SagaOrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sagas")
public class SagaController {

    @Autowired
    private SagaOrchestratorService sagaService;

    @GetMapping("/{sagaId}")
    public ResponseEntity<SagaTransaction> getSaga(@PathVariable String sagaId) {
        return sagaService.getSaga(sagaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
