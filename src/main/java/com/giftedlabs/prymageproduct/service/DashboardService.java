package com.giftedlabs.prymageproduct.service;

import com.giftedlabs.prymageproduct.dto.response.DashboardStatsResponse;
import com.giftedlabs.prymageproduct.dto.response.TicketSummaryResponse;
import com.giftedlabs.prymageproduct.entity.Ticket;
import com.giftedlabs.prymageproduct.entity.User;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.enums.TicketPriority;
import com.giftedlabs.prymageproduct.enums.TicketStatus;
import com.giftedlabs.prymageproduct.exception.ResourceNotFoundException;
import com.giftedlabs.prymageproduct.repository.TicketRepository;
import com.giftedlabs.prymageproduct.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DashboardService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public DashboardService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse stats(Role role, UUID userId) {
        if (role == Role.CUSTOMER) {
            User customer = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return new DashboardStatsResponse(
                    null,
                    ticketRepository.countByCustomerUserAndStatus(customer, TicketStatus.OPEN),
                    ticketRepository.countByCustomerUserAndStatus(customer, TicketStatus.IN_PROGRESS),
                    ticketRepository.countByCustomerUserAndStatus(customer, TicketStatus.RESOLVED),
                    ticketRepository.countByCustomerUserAndStatus(customer, TicketStatus.CLOSED),
                    null,
                    null,
                    null,
                    null,
                    ticketRepository.findTop5ByCustomerUserOrderByCreatedAtDesc(customer).stream().map(this::toSummary).toList(),
                    List.of(),
                    ticketRepository.countByCustomerUser(customer)
            );
        }

        long total = ticketRepository.count();
        long open = ticketRepository.countByStatus(TicketStatus.OPEN);
        long inProgress = ticketRepository.countByStatus(TicketStatus.IN_PROGRESS);
        long resolved = ticketRepository.countByStatus(TicketStatus.RESOLVED);
        long closed = ticketRepository.countByStatus(TicketStatus.CLOSED);

        Map<String, Long> byPriority = new LinkedHashMap<>();
        for (TicketPriority p : TicketPriority.values()) {
            byPriority.put(p.name(), ticketRepository.countByPriority(p));
        }
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (TicketStatus s : TicketStatus.values()) {
            byStatus.put(s.name(), ticketRepository.countByStatus(s));
        }

        Double avgHours = ticketRepository.findAll().stream()
                .filter(t -> t.getResolvedAt() != null)
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getResolvedAt()).toMinutes())
                .average().orElse(0.0) / 60.0;

        return new DashboardStatsResponse(
                total, open, inProgress, resolved, closed,
                ticketRepository.countByPriorityAndStatus(TicketPriority.CRITICAL, TicketStatus.OPEN),
                avgHours,
                byPriority,
                byStatus,
                ticketRepository.findTop5ByOrderByCreatedAtDesc().stream().map(this::toSummary).toList(),
                List.of(),
                null
        );
    }

    private TicketSummaryResponse toSummary(Ticket t) {
        return new TicketSummaryResponse(
                t.getId(), t.getTicketNumber(), t.getIssueTitle(), t.getName(), t.getEmail(),
                t.getPriority(), t.getStatus(), t.getAssignedTo() == null ? null : t.getAssignedTo().getId(), t.getCreatedAt());
    }
}
