package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.free;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fields/{fieldName}/free-slots")
@Tag(name = "Free Slots")
public class FreeSlotsRestController {
    private final FreeSlotsService freeSlotsService;

    public FreeSlotsRestController(FreeSlotsService freeSlotsService) {
        this.freeSlotsService = freeSlotsService;
    }

    @GetMapping("/{numberOfDays}")
    @Operation(summary = "Gets a page of")
    @ResponseStatus(HttpStatus.OK)
    Page<FreeSlotDTO> getFreeSlots(
            @Valid @PathVariable String fieldName,
            @Valid @Positive @PathVariable int numberOfDays,
            @PageableDefault(page = 0, size = 10, sort = "date")
            @Valid @ParameterObject Pageable pageable
    ) throws ItemNotFoundException {
        return freeSlotsService.getFreeSlots(fieldName, numberOfDays, pageable);
    }
}
