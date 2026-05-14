package com.giftedlabs.prymageproduct.repository;

import com.giftedlabs.prymageproduct.entity.Ticket;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByTicketNumberAndEmailIgnoreCase(String ticketNumber, String email);
}
