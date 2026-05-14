package com.giftedlabs.prymageproduct.repository;

import com.giftedlabs.prymageproduct.entity.Ticket;
import com.giftedlabs.prymageproduct.entity.User;
import com.giftedlabs.prymageproduct.enums.TicketPriority;
import com.giftedlabs.prymageproduct.enums.TicketStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByTicketNumberAndEmailIgnoreCase(String ticketNumber, String email);
    Page<Ticket> findByCustomerUser(User customerUser, Pageable pageable);

    @Query("""
            SELECT t FROM Ticket t
            WHERE (:status IS NULL OR t.status = :status)
              AND (:priority IS NULL OR t.priority = :priority)
              AND (:assignedTo IS NULL OR t.assignedTo.id = :assignedTo)
              AND (
                :search IS NULL OR
                lower(t.ticketNumber) LIKE lower(concat('%', :search, '%')) OR
                lower(t.name) LIKE lower(concat('%', :search, '%')) OR
                lower(t.email) LIKE lower(concat('%', :search, '%')) OR
                lower(t.issueTitle) LIKE lower(concat('%', :search, '%'))
              )
            """)
    Page<Ticket> searchTickets(
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority,
            @Param("assignedTo") UUID assignedTo,
            @Param("search") String search,
            Pageable pageable
    );

    long countByStatus(TicketStatus status);
    long countByPriority(TicketPriority priority);
    long countByPriorityAndStatus(TicketPriority priority, TicketStatus status);
    long countByCustomerUser(User customerUser);
    long countByCustomerUserAndStatus(User customerUser, TicketStatus status);
    List<Ticket> findTop5ByOrderByCreatedAtDesc();
    List<Ticket> findTop5ByCustomerUserOrderByCreatedAtDesc(User customerUser);
}
