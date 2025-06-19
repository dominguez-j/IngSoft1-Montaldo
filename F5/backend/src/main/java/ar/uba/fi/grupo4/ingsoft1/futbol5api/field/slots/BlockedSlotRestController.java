package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.PermissionDeniedException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/fields/blocked-slots")
@Tag(name = "Blocked Slots")
public class BlockedSlotRestController {
    private final BlockedSlotService blockedSlotService;

    @Autowired
    public BlockedSlotRestController(BlockedSlotService blockedSlotService) {
        this.blockedSlotService = blockedSlotService;
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Block a specific slot")
    public ResponseEntity<BlockedSlotDTO> createBlockedSlot(@Valid @RequestBody BlockedSlotCreateDTO data) {
        BlockedSlotDTO result = blockedSlotService.createBlockedSlot(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/history")
    @Operation(summary = "Get all blocked slots reserved by the authenticated user, filtered by past or future, ordered by date")
    public ResponseEntity<Page<BlockedSlotDTO>> getAllMyBlockedSlots(
            @RequestParam(required = false, defaultValue = "true") Boolean isCurrent,
            @PageableDefault(page = 0, size = 10)
            @Valid @ParameterObject Pageable pageable
    ) {
        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("date").descending());
        Page<BlockedSlotDTO> result = blockedSlotService.findAllMyBlockedSlots(isCurrent, sorted);
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a blocked slot if the authenticated user owns the field")
    public  ResponseEntity<Void> deleteBlockedSlot(@PathVariable Long id) throws ItemNotFoundException,PermissionDeniedException {
        blockedSlotService.deleteBlockedSlotByOwner(id);
        return ResponseEntity.noContent().build();
    }



}
