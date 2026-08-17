package com.danieloliveira.order_management.scheduler;

import com.danieloliveira.order_management.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final AlertService alertService;

    @Scheduled(fixedRateString = "${scheduler.interval}")
    public void executar() {
        alertService.verificarPrazos();
    }
}
