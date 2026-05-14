package com.giftedlabs.prymageproduct.service;

import com.giftedlabs.prymageproduct.dto.request.PublicTicketRequest;
import com.giftedlabs.prymageproduct.dto.response.PublicTicketCreatedResponse;
import com.giftedlabs.prymageproduct.dto.response.PublicTicketReplyResponse;
import com.giftedlabs.prymageproduct.dto.response.PublicTicketTrackResponse;
import com.giftedlabs.prymageproduct.entity.Ticket;
import com.giftedlabs.prymageproduct.enums.NoteType;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.enums.TicketStatus;
import com.giftedlabs.prymageproduct.exception.ResourceNotFoundException;
import com.giftedlabs.prymageproduct.repository.TicketNoteRepository;
import com.giftedlabs.prymageproduct.repository.TicketRepository;
import com.giftedlabs.prymageproduct.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicTicketService {

    private final TicketRepository ticketRepository;
    private final TicketNoteRepository ticketNoteRepository;
    private final TicketNumberGeneratorService ticketNumberGeneratorService;
    private final UserRepository userRepository;

    public PublicTicketService(
            TicketRepository ticketRepository,
            TicketNoteRepository ticketNoteRepository,
            TicketNumberGeneratorService ticketNumberGeneratorService,
            UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketNoteRepository = ticketNoteRepository;
        this.ticketNumberGeneratorService = ticketNumberGeneratorService;
        this.userRepository = userRepository;
    }

    @Transactional
    public PublicTicketCreatedResponse submit(PublicTicketRequest request) {
        String ticketNumber = ticketNumberGeneratorService.nextTicketNumber();

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setName(request.name().trim());
        ticket.setEmail(request.email().trim().toLowerCase());
        ticket.setIssueTitle(request.issueTitle().trim());
        ticket.setDescription(request.description().trim());
        ticket.setPriority(request.priority());
        ticket.setStatus(TicketStatus.OPEN);
        userRepository.findByEmailIgnoreCase(request.email().trim())
                .filter(u -> u.getRole() == Role.CUSTOMER)
                .ifPresent(ticket::setCustomerUser);

        ticketRepository.save(ticket);

        return new PublicTicketCreatedResponse(
                ticketNumber,
                "Your ticket has been submitted successfully. Reference number: " + ticketNumber
        );
    }

    @Transactional(readOnly = true)
    public PublicTicketTrackResponse track(String ticketNumber, String email) {
        Ticket ticket = ticketRepository.findByTicketNumberAndEmailIgnoreCase(ticketNumber, email)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found for the provided ticket number and email"));

        List<PublicTicketReplyResponse> replies = ticketNoteRepository
                .findByTicketAndNoteTypeOrderByCreatedAtAsc(ticket, NoteType.CUSTOMER_REPLY)
                .stream()
                .map(note -> new PublicTicketReplyResponse(note.getContent(), note.getCreatedAt(), note.getNoteType()))
                .toList();

        return new PublicTicketTrackResponse(
                ticket.getTicketNumber(),
                ticket.getIssueTitle(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedAt(),
                replies
        );
    }
}
