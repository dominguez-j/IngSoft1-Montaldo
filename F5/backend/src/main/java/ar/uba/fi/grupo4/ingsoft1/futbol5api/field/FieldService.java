package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.UniqueConstraintViolationException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed.ClosedMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.closed.ClosedMatchRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.OpenMatch;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.match.open.OpenMatchRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FieldService {
    private final FieldRepository fieldRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final OpenMatchRepository openMatchRepository;
    private final ClosedMatchRepository closedMatchRepository;

    public FieldService(FieldRepository fieldRepository, AuthenticatedUserProvider authenticatedUserProvider, OpenMatchRepository openMatchRepository, ClosedMatchRepository closedMatchRepository) {
        this.fieldRepository = fieldRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.openMatchRepository = openMatchRepository;
        this.closedMatchRepository = closedMatchRepository;
    }

    public Page<FieldDTO> getFields(FieldFilterDTO filterDTO, Pageable pageable) {
        Specification<Field> specification = FieldSpecification.getSpecification(filterDTO, authenticatedUserProvider.getAuthenticatedUser());
        return fieldRepository.findAll(specification, pageable).map(FieldDTO::new);
    }

    public void createField(FieldCreateDTO dto) throws UniqueConstraintViolationException {
        if (fieldRepository.existsByName(dto.name())) {
            throw new UniqueConstraintViolationException("Field name already registered");
        }
        fieldRepository.save(dto.asField(authenticatedUserProvider::getAuthenticatedUser));
    }

    public void deleteField(String fieldName) throws ItemNotFoundException, PermissionDeniedException {
        Field field = fieldRepository.findByName(fieldName)
                .orElseThrow(() -> new ItemNotFoundException("Field not found: " + fieldName));
        User user = authenticatedUserProvider.getAuthenticatedUser();
        if (!field.getOwner().getEmail().equals(user.getEmail())) {
            throw new PermissionDeniedException("User does not own field with name " + fieldName);
        }
        fieldRepository.delete(field);
    }

    public void updateField(String fieldName, FieldUpdateDTO dto) throws ItemNotFoundException, PermissionDeniedException {
        Field field = fieldRepository.findByName(fieldName)
                .orElseThrow(() -> new ItemNotFoundException("Field name not registered"));

        User user = authenticatedUserProvider.getAuthenticatedUser();
        if (!field.getOwner().getEmail().equals(user.getEmail())) {
            throw new PermissionDeniedException("User does not own field with name " + fieldName);
        }

        Field updatedField = dto.asField(field.getId(), authenticatedUserProvider::getAuthenticatedUser);
        fieldRepository.save(updatedField);
    }

    public List<BlockedSlot> getBlockedSlotsOfMyFields() {
        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        List<Field> fields = fieldRepository.findByOwner(currentUser, Pageable.unpaged()).getContent();

        List<ClosedMatch> closedMatches = closedMatchRepository.findByFieldIn(fields);
        List<OpenMatch> openMatches = openMatchRepository.findByFieldIn(fields);

        List<BlockedSlot> blockedSlots = new ArrayList<>();

        for (ClosedMatch match : closedMatches) {
            BlockedSlot slot = match.getBlockedSlot();
            if (slot != null) {
                blockedSlots.add(slot);
            }
        }

        for (OpenMatch match : openMatches) {
            BlockedSlot slot = match.getBlockedSlot();
            if (slot != null) {
                blockedSlots.add(slot);
            }
        }

        return blockedSlots;
    }

}
