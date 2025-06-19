package ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open;


import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.BusinessRuleException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.free.FreeSlotsService;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class OpenMatchService {
    private final OpenMatchRepository openMatchRepository;
    private final FieldRepository fieldRepository;
    private final FreeSlotsService freeSlotsService;
    private final BlockedSlotRepository blockedSlotRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    public OpenMatchService(OpenMatchRepository openMatchRepository, FieldRepository fieldRepository, FreeSlotsService freeSlotsService, BlockedSlotRepository blockedSlotRepository, AuthenticatedUserProvider authenticatedUserProvider) {
        this.openMatchRepository = openMatchRepository;
        this.fieldRepository = fieldRepository;
        this.freeSlotsService = freeSlotsService;
        this.blockedSlotRepository = blockedSlotRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public OpenMatchDTO createOpenMatch(OpenMatchCreateDTO dto) {
        OpenMatch openMatch = dto.asOpenMatch(
                fieldRepository::findByName,
                blockedSlotRepository::findById,
                authenticatedUserProvider::getAuthenticatedUser
        );
        
        if (dto.minPlayers() > dto.maxPlayers()) {
            throw new IllegalArgumentException("The minimum number of players must be less than or equal to the maximum number of players.");
        }

        openMatch.setPlayers(new ArrayList<>());
        openMatch.setConfirmed(false);

        openMatchRepository.save(openMatch);
        return new OpenMatchDTO(openMatch);
    }

    public Page<OpenMatchDTO> getOpenMatches(OpenMatchFilterDTO filterDTO, Pageable pageable) {
        Specification<OpenMatch> specification = OpenMatchSpecification.getSpecification(filterDTO, authenticatedUserProvider.getAuthenticatedUser());
        return openMatchRepository.findAll(specification, pageable).map(OpenMatchDTO::new);
    }

    public OpenMatchDTO getOpenMatchById(Long id) throws ItemNotFoundException {
        OpenMatch openMatch = openMatchRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Open match not found: " + id));
        return new OpenMatchDTO(openMatch);
    }

    public void deleteOpenMatch(Long id) throws ItemNotFoundException, PermissionDeniedException {
        OpenMatch match = openMatchRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Open match not found: " + id));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        if (!match.getOwner().getEmail().equals(currentUser.getEmail())) {
            throw new PermissionDeniedException("Only the match owner can delete this match.");
        }

        openMatchRepository.deleteById(id);
    }

    public void confirmOpenMatch(Long id) throws ItemNotFoundException, PermissionDeniedException {
        OpenMatch match = openMatchRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Open match not found: " + id));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        if (!match.getOwner().getEmail().equals(currentUser.getEmail())) {
            throw new PermissionDeniedException("Only the match owner can confirm this match.");
        }

        if (match.getPlayers().size() < match.getMinPlayers()) {
            throw new BusinessRuleException("The minimum number of players has not been reached.");
        }


        if (match.getPlayers().size() % 2 != 0) {
            throw new BusinessRuleException("Players count must be even to form balanced teams");
        }

        match.setConfirmed(true);
        openMatchRepository.save(match);
    }

    public OpenMatchDTO getByBlockedSlotId(Long blockedSlotId) throws ItemNotFoundException {
        OpenMatch match = openMatchRepository.findByBlockedSlot_Id(blockedSlotId)
                .orElseThrow(() -> new ItemNotFoundException("No open match found for blocked slot id: " + blockedSlotId));
        return new OpenMatchDTO(match);
    }

    @Transactional
    public void joinOpenMatch(Long matchId) throws ItemNotFoundException, BusinessRuleException {
        OpenMatch match = openMatchRepository.findById(matchId)
                .orElseThrow(() -> new ItemNotFoundException("Open match not found: " + matchId));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        if (match.getPlayers().contains(currentUser)) {
            throw new BusinessRuleException("User is already registered in this match.");
        }

        if (match.getPlayers().size() >= match.getMaxPlayers()) {
            throw new BusinessRuleException("Maximum number of players already reached.");
        }

        if (match.getConfirmed()) {
            throw new BusinessRuleException("Cannot join a confirmed match.");
        }

        LocalDate matchDate = match.getBlockedSlot().getDate();
        LocalTime matchStartTime = match.getBlockedSlot().getStartTime();
        LocalDateTime matchStartDateTime = LocalDateTime.of(matchDate, matchStartTime);
        if (LocalDateTime.now().isAfter(matchStartDateTime)) {
            throw new BusinessRuleException("Cannot join a match that has already started.");
        }

        match.getPlayers().add(currentUser);

        openMatchRepository.save(match);
    }

    @Transactional
    public void leaveOpenMatch(Long matchId) throws ItemNotFoundException, PermissionDeniedException {
        OpenMatch match = openMatchRepository.findById(matchId)
                .orElseThrow(() -> new ItemNotFoundException("Open match not found: " + matchId));

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        if (!match.getPlayers().contains(currentUser)) {
            throw new PermissionDeniedException("You are not part of this match.");
        }

        if (match.getConfirmed()) {
            throw new PermissionDeniedException("Cannot leave a confirmed match.");
        }

        match.getPlayers().remove(currentUser);

        openMatchRepository.save(match);
    }

    public Page<OpenMatchDTO> getMyParticipations(Boolean isCurrent, Pageable pageable) {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        Specification<OpenMatch> spec = OpenMatchPlayerSpecification.getSpecification(isCurrent, user);
        return openMatchRepository.findAll(spec, pageable).map(OpenMatchDTO::new);
    }
}