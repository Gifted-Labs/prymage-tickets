package com.giftedlabs.prymageproduct.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Year;
import org.springframework.stereotype.Service;

@Service
public class TicketNumberGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String nextTicketNumber() {
        int year = Year.now().getValue();
        Integer lastSequence;

        try {
            lastSequence = (Integer) entityManager.createNativeQuery(
                            "SELECT last_sequence FROM ticket_sequences WHERE year = :year FOR UPDATE")
                    .setParameter("year", year)
                    .getSingleResult();
        } catch (NoResultException ex) {
            entityManager.createNativeQuery("INSERT INTO ticket_sequences(year, last_sequence) VALUES (:year, 0)")
                    .setParameter("year", year)
                    .executeUpdate();
            lastSequence = 0;
        }

        int next = lastSequence + 1;
        entityManager.createNativeQuery("UPDATE ticket_sequences SET last_sequence = :seq WHERE year = :year")
                .setParameter("seq", next)
                .setParameter("year", year)
                .executeUpdate();

        return "TKT-" + year + String.format("%04d", next);
    }
}
