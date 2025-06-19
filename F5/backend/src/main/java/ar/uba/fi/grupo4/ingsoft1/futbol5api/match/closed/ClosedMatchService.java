package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.team.TeamRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@Transactional
public class ClosedMatchService {

    private final ClosedMatchRepository    closedMatchRepository;
    private final TeamRepository           teamRepository;
    private final FieldRepository          fieldRepository;
    private final BlockedSlotRepository    blockedSlotRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    public ClosedMatchService(
            ClosedMatchRepository closedMatchRepository,
            TeamRepository teamRepository,
            FieldRepository fieldRepository,
            BlockedSlotRepository blockedSlotRepository,
            AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.closedMatchRepository       = closedMatchRepository;
        this.teamRepository              = teamRepository;
        this.fieldRepository             = fieldRepository;
        this.blockedSlotRepository       = blockedSlotRepository;
        this.authenticatedUserProvider   = authenticatedUserProvider;
    }

    public ClosedMatchDTO createClosedMatch(ClosedMatchCreateDTO dto) throws ItemNotFoundException {
        Field cancha = fieldRepository.findByName(dto.fieldName())
                .orElseThrow(() -> new ItemNotFoundException(
                        "Field not found: " + dto.fieldName()
                ));

        BlockedSlot slot = blockedSlotRepository.findById(dto.blockedSlotId())
                .orElseThrow(() -> new ItemNotFoundException(
                        "BlockedSlot not found: " + dto.blockedSlotId()
                ));

        if (!slot.getField().getId().equals(cancha.getId())) {
            throw new IllegalArgumentException(
                    "BlockedSlot " + dto.blockedSlotId() +
                            " does not belong to field " + dto.fieldName()
            );
        }

        ClosedMatch closedMatch = dto.asClosedMatch(
                name -> Optional.of(cancha),
                id   -> Optional.of(slot),
                authenticatedUserProvider::getAuthenticatedUser,
                teamRepository::findByTeamName
        );
        ClosedMatch saved = closedMatchRepository.save(closedMatch);

        return new ClosedMatchDTO(saved);
    }
}
