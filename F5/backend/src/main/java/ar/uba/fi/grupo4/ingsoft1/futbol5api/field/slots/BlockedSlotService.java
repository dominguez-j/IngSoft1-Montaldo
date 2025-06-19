package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.UniqueConstraintViolationException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.AuthenticatedUserProvider;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BlockedSlotService {
    private final BlockedSlotRepository blockedSlotRepository;
    private final FieldRepository fieldRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public BlockedSlotService(BlockedSlotRepository blockedSlotRepository, FieldRepository fieldRepository, AuthenticatedUserProvider authenticatedUserProvider, ScheduleRepository scheduleRepository) {
        this.blockedSlotRepository = blockedSlotRepository;
        this.fieldRepository = fieldRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.scheduleRepository = scheduleRepository;
    }

    public BlockedSlotDTO createBlockedSlot(BlockedSlotCreateDTO dto) {
        if (blockedSlotRepository.existsByField_NameAndSlotNumberAndDate(
                dto.fieldName(),
                dto.slotNumber(),
                dto.date()
        )) {
            throw new UniqueConstraintViolationException(
                    "Cannot block slot number " + dto.slotNumber()
                            + " in field " + dto.fieldName() + " on date " + dto.date()
            );
        }
        BlockedSlot blockedSlot = dto.asBlockedSlot(
                fieldRepository::findByName,
                scheduleRepository::findScheduleByFieldAndDayOfWeek,
                authenticatedUserProvider::getAuthenticatedUser
        );

        blockedSlotRepository.save(blockedSlot);

        return new BlockedSlotDTO(blockedSlot);
    }

    public Page<BlockedSlotDTO> findAllMyBlockedSlots(Boolean isCurrent, Pageable pageable) {
        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        Specification<BlockedSlot> specification = BlockedSlotSpecification.getSpecification(isCurrent, currentUser);
        Page<BlockedSlot> slots = blockedSlotRepository.findAll(specification, pageable);

        return slots.map(BlockedSlotDTO::new);
    }

    @Transactional
    public void deleteBlockedSlotByOwner(Long blockedSlotId) throws ItemNotFoundException, PermissionDeniedException {
        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        BlockedSlot blockedSlot = blockedSlotRepository.findById(blockedSlotId)
                .orElseThrow(() -> new ItemNotFoundException("Blocked slot not found"));

        Field field = blockedSlot.getField();
        if (!field.getOwner().getId().equals(currentUser.getId())) {
            throw new PermissionDeniedException("You do not have permission to delete this blocked slot");
        }

        blockedSlotRepository.delete(blockedSlot);
        blockedSlotRepository.flush();
    }
}
