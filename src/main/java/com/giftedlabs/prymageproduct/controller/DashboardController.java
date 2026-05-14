package com.giftedlabs.prymageproduct.controller;

import com.giftedlabs.prymageproduct.dto.response.DashboardStatsResponse;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.security.AppUserDetails;
import com.giftedlabs.prymageproduct.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> stats(@AuthenticationPrincipal AppUserDetails user) {
        return ResponseEntity.ok(dashboardService.stats(Role.valueOf(user.getRole()), user.getId()));
    }
}
