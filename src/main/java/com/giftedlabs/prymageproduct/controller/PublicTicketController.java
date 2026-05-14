package com.giftedlabs.prymageproduct.controller;

import com.giftedlabs.prymageproduct.dto.request.PublicTicketRequest;
import com.giftedlabs.prymageproduct.dto.response.PublicTicketCreatedResponse;
import com.giftedlabs.prymageproduct.dto.response.PublicTicketTrackResponse;
import com.giftedlabs.prymageproduct.service.PublicTicketService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets/public")
@Validated
public class PublicTicketController {

    private final PublicTicketService publicTicketService;

    public PublicTicketController(PublicTicketService publicTicketService) {
        this.publicTicketService = publicTicketService;
    }

    @Operation(summary = "Submit a public support ticket")
    @PostMapping
    public ResponseEntity<PublicTicketCreatedResponse> submit(@Valid @RequestBody PublicTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicTicketService.submit(request));
    }

    @Operation(summary = "Track ticket by ticket number and email")
    @GetMapping("/track/{ticketNumber}")
    public ResponseEntity<PublicTicketTrackResponse> track(
            @PathVariable @NotBlank String ticketNumber,
            @RequestParam @NotBlank @Email String email
    ) {
        return ResponseEntity.ok(publicTicketService.track(ticketNumber, email));
    }
}
