package com.giftedlabs.prymageproduct.repository;

import com.giftedlabs.prymageproduct.entity.Ticket;
import com.giftedlabs.prymageproduct.entity.TicketNote;
import com.giftedlabs.prymageproduct.enums.NoteType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketNoteRepository extends JpaRepository<TicketNote, UUID> {
    List<TicketNote> findByTicketAndNoteTypeOrderByCreatedAtAsc(Ticket ticket, NoteType noteType);
}
