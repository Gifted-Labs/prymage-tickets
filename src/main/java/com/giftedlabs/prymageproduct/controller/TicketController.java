package com.giftedlabs.prymageproduct.controller;

import com.giftedlabs.prymageproduct.dto.request.AssignTicketRequest;
import com.giftedlabs.prymageproduct.dto.request.NoteRequest;
import com.giftedlabs.prymageproduct.dto.request.StatusChangeRequest;
import com.giftedlabs.prymageproduct.dto.response.NoteResponse;
import com.giftedlabs.prymageproduct.dto.response.PagedResponse;
import com.giftedlabs.prymageproduct.dto.response.TicketDetailResponse;
import com.giftedlabs.prymageproduct.dto.response.TicketSummaryResponse;
import com.giftedlabs.prymageproduct.enums.Role;
import com.giftedlabs.prymageproduct.enums.TicketPriority;
import com.giftedlabs.prymageproduct.enums.TicketStatus;
import com.giftedlabs.prymageproduct.security.AppUserDetails;
import com.giftedlabs.prymageproduct.service.TicketService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TicketSummaryResponse>> list(
            @AuthenticationPrincipal AppUserDetails user,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) UUID assignedTo,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        return ResponseEntity.ok(ticketService.listTickets(Role.valueOf(user.getRole()), user.getId(), status, priority, assignedTo, search, page, size, sort, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailResponse> getById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails user) {
        return ResponseEntity.ok(ticketService.getTicket(id, Role.valueOf(user.getRole()), user.getId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketDetailResponse> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusChangeRequest request,
            @AuthenticationPrincipal AppUserDetails user
    ) {
        return ResponseEntity.ok(ticketService.changeStatus(id, request, user.getId(), Role.valueOf(user.getRole())));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketDetailResponse> assign(
            @PathVariable UUID id,
            @Valid @RequestBody AssignTicketRequest request,
            @AuthenticationPrincipal AppUserDetails user
    ) {
        return ResponseEntity.ok(ticketService.assignTicket(id, request, user.getId(), Role.valueOf(user.getRole())));
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<NoteResponse> addNote(
            @PathVariable UUID id,
            @Valid @RequestBody NoteRequest request,
            @AuthenticationPrincipal AppUserDetails user
    ) {
        return ResponseEntity.ok(ticketService.addNote(id, request, user.getId(), Role.valueOf(user.getRole())));
    }
}
