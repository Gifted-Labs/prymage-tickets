package com.giftedlabs.prymageproduct.service;

import com.giftedlabs.prymageproduct.dto.request.AssignTicketRequest;
import com.giftedlabs.prymageproduct.dto.request.NoteRequest;
import com.giftedlabs.prymageproduct.dto.request.StatusChangeRequest;
import com.giftedlabs.prymageproduct.dto.response.NoteResponse;
import com.giftedlabs.prymageproduct.dto.response.PagedResponse;
import com.giftedlabs.prymageproduct.dto.response.TicketDetailResponse;
import com.giftedlabs.prymageproduct.dto.response.TicketSummaryResponse;
import com.giftedlabs.prymageproduct.entity.AuditLog;
import com.giftedlabs.prymageproduct.entity.Ticket;
import com.giftedlabs.prymageproduct.entity.TicketNote;
import com.giftedlabs.prymageproduct.entity.User;
import com.giftedlabs.prymageproduct.enums.NoteType;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.enums.TicketPriority;
import com.giftedlabs.prymageproduct.enums.TicketStatus;
import com.giftedlabs.prymageproduct.exception.ForbiddenOperationException;
import com.giftedlabs.prymageproduct.exception.ResourceNotFoundException;
import com.giftedlabs.prymageproduct.repository.AuditLogRepository;
import com.giftedlabs.prymageproduct.repository.TicketNoteRepository;
import com.giftedlabs.prymageproduct.repository.TicketRepository;
import com.giftedlabs.prymageproduct.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketNoteRepository ticketNoteRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(TicketStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(TicketStatus.OPEN, Set.of(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, TicketStatus.CLOSED));
        ALLOWED_TRANSITIONS.put(TicketStatus.IN_PROGRESS, Set.of(TicketStatus.RESOLVED, TicketStatus.CLOSED, TicketStatus.OPEN));
        ALLOWED_TRANSITIONS.put(TicketStatus.RESOLVED, Set.of(TicketStatus.CLOSED, TicketStatus.OPEN));
        ALLOWED_TRANSITIONS.put(TicketStatus.CLOSED, Set.of(TicketStatus.OPEN));
    }

    public TicketService(TicketRepository ticketRepository, TicketNoteRepository ticketNoteRepository, UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketNoteRepository = ticketNoteRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<TicketSummaryResponse> listTickets(Role role, UUID userId, TicketStatus status, TicketPriority priority, UUID assignedTo, String search, int page, int size, String sort, String direction) {
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        Sort.Direction dir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), normalizedSize, Sort.by(dir, mapSortField(sort)));

        Page<Ticket> results;
        if (role == Role.CUSTOMER) {
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            results = ticketRepository.findByCustomerUser(user, pageable);
        } else {
            results = ticketRepository.searchTickets(status, priority, assignedTo, emptyToNull(search), pageable);
        }

        List<TicketSummaryResponse> content = results.getContent().stream().map(this::toSummary).toList();
        return new PagedResponse<>(content, results.getNumber(), results.getSize(), results.getTotalElements(), results.getTotalPages(), results.isFirst(), results.isLast());
    }

    @Transactional(readOnly = true)
    public TicketDetailResponse getTicket(UUID ticketId, Role role, UUID userId) {
        Ticket ticket = findTicket(ticketId);
        enforceTicketViewAccess(ticket, role, userId);

        List<TicketNote> notes = ticketNoteRepository.findByTicketOrderByCreatedAtAsc(ticket);
        List<NoteResponse> noteResponses = notes.stream()
                .filter(n -> role != Role.CUSTOMER || n.getNoteType() == NoteType.CUSTOMER_REPLY)
                .map(this::toNote)
                .toList();

        return toDetail(ticket, noteResponses);
    }

    @Transactional
    public TicketDetailResponse changeStatus(UUID ticketId, StatusChangeRequest request, UUID actorId, Role actorRole) {
        ensureStaffOrAdmin(actorRole);

        Ticket ticket = findTicket(ticketId);
        TicketStatus current = ticket.getStatus();
        TicketStatus next = request.status();

        if (current != next && !ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(next)) {
            throw new ForbiddenOperationException("Invalid status transition from " + current + " to " + next);
        }

        ticket.setStatus(next);
        ticket.setResolvedAt(next == TicketStatus.RESOLVED || next == TicketStatus.CLOSED ? OffsetDateTime.now() : null);

        User actor = userRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        auditLogRepository.save(newAudit(ticket, actor, "STATUS_CHANGED", current.name(), next.name()));

        return getTicket(ticket.getId(), actorRole, actorId);
    }

    @Transactional
    public TicketDetailResponse assignTicket(UUID ticketId, AssignTicketRequest request, UUID actorId, Role actorRole) {
        if (actorRole != Role.ADMIN) {
            throw new ForbiddenOperationException("Only ADMIN can assign tickets");
        }

        Ticket ticket = findTicket(ticketId);
        User staff = userRepository.findById(request.staffUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));

        if (staff.getRole() != Role.STAFF) {
            throw new ForbiddenOperationException("Assigned user must have STAFF role");
        }

        UUID oldAssigned = ticket.getAssignedTo() == null ? null : ticket.getAssignedTo().getId();
        ticket.setAssignedTo(staff);

        User actor = userRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        auditLogRepository.save(newAudit(ticket, actor, "ASSIGNED", oldAssigned == null ? null : oldAssigned.toString(), staff.getId().toString()));

        return getTicket(ticket.getId(), actorRole, actorId);
    }

    @Transactional
    public NoteResponse addNote(UUID ticketId, NoteRequest request, UUID actorId, Role actorRole) {
        ensureStaffOrAdmin(actorRole);

        Ticket ticket = findTicket(ticketId);
        User actor = userRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        TicketNote note = new TicketNote();
        note.setTicket(ticket);
        note.setAuthor(actor);
        note.setNoteType(request.noteType());
        note.setContent(request.content().trim());

        TicketNote saved = ticketNoteRepository.save(note);
        auditLogRepository.save(newAudit(ticket, actor, "NOTE_ADDED", null, request.noteType().name()));

        return toNote(saved);
    }

    private void ensureStaffOrAdmin(Role role) {
        if (role != Role.ADMIN && role != Role.STAFF) {
            throw new ForbiddenOperationException("Only STAFF or ADMIN can perform this action");
        }
    }

    private void enforceTicketViewAccess(Ticket ticket, Role role, UUID userId) {
        if (role == Role.CUSTOMER) {
            if (ticket.getCustomerUser() == null || !ticket.getCustomerUser().getId().equals(userId)) {
                throw new ForbiddenOperationException("You can only view your own tickets");
            }
        }
    }

    private Ticket findTicket(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
    }

    private String mapSortField(String sort) {
        return switch (sort == null ? "createdAt" : sort) {
            case "ticketNumber" -> "ticketNumber";
            case "priority" -> "priority";
            case "status" -> "status";
            case "updatedAt" -> "updatedAt";
            default -> "createdAt";
        };
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private TicketSummaryResponse toSummary(Ticket t) {
        return new TicketSummaryResponse(
                t.getId(),
                t.getTicketNumber(),
                t.getIssueTitle(),
                t.getName(),
                t.getEmail(),
                t.getPriority(),
                t.getStatus(),
                t.getAssignedTo() == null ? null : t.getAssignedTo().getId(),
                t.getCreatedAt()
        );
    }

    private TicketDetailResponse toDetail(Ticket t, List<NoteResponse> notes) {
        return new TicketDetailResponse(
                t.getId(),
                t.getTicketNumber(),
                t.getName(),
                t.getEmail(),
                t.getIssueTitle(),
                t.getDescription(),
                t.getPriority(),
                t.getStatus(),
                t.getAssignedTo() == null ? null : t.getAssignedTo().getId(),
                t.getCustomerUser() == null ? null : t.getCustomerUser().getId(),
                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.getResolvedAt(),
                notes
        );
    }

    private NoteResponse toNote(TicketNote n) {
        return new NoteResponse(
                n.getId(),
                n.getContent(),
                n.getNoteType(),
                n.getAuthor().getId(),
                n.getAuthor().getFullName(),
                n.getCreatedAt()
        );
    }

    private AuditLog newAudit(Ticket ticket, User changedBy, String action, String oldValue, String newValue) {
        AuditLog log = new AuditLog();
        log.setTicket(ticket);
        log.setChangedBy(changedBy);
        log.setAction(action);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        return log;
    }
}
