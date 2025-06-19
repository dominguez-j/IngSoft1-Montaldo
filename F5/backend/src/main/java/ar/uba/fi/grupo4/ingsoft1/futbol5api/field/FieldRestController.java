package ar.uba.fi.grupo4.ingsoft1.futbol5api.field;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.UniqueConstraintViolationException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.user.User;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fields")
@Tag(name = "Fields")
public class FieldRestController {
    private final FieldService fieldService;

    @Autowired
    public FieldRestController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Gets a list of fields")
    public Page<FieldDTO> getFields(
            FieldFilterDTO filterDTO,
            @PageableDefault(page = 0, size = 10)
            @Valid @ParameterObject Pageable pageable
    ) {
        return fieldService.getFields(filterDTO, pageable);
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Registers a new field")
    public ResponseEntity<Void> createField(
            @Valid @RequestBody FieldCreateDTO data
    ) {
        fieldService.createField(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{fieldName}")
    @Operation(summary = "Deletes a field by name")
    public ResponseEntity<Void> deleteField(@PathVariable String fieldName) throws ItemNotFoundException, PermissionDeniedException {
        fieldService.deleteField(fieldName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{fieldName}")
    @Operation(summary = "Updates an existing field")
    public ResponseEntity<Void> updateField(
            @PathVariable String fieldName, @Valid @RequestBody FieldUpdateDTO dto
    ) throws ItemNotFoundException, PermissionDeniedException {
        fieldService.updateField(fieldName, dto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-blocked-slots")
    public List<BlockedSlotDTO> getMyBlockedSlots(@AuthenticationPrincipal User currentUser) {
        return fieldService.getBlockedSlotsOfMyFields().stream()
                .map(BlockedSlotDTO::new)
                .toList();
    }

}
