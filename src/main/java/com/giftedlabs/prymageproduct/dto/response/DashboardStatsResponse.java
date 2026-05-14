package com.giftedlabs.prymageproduct.dto.response;

import java.util.List;
import java.util.Map;

public record DashboardStatsResponse(
        Long totalTickets,
        Long openTickets,
        Long inProgressTickets,
        Long resolvedTickets,
        Long closedTickets,
        Long criticalOpen,
        Double avgResolutionTimeHours,
        Map<String, Long> ticketsByPriority,
        Map<String, Long> ticketsByStatus,
        List<TicketSummaryResponse> recentTickets,
        List<StaffPerformanceResponse> staffPerformance,
        Long myTickets
) {
    public record StaffPerformanceResponse(String staffName, Long resolved, Long inProgress) {}
}
