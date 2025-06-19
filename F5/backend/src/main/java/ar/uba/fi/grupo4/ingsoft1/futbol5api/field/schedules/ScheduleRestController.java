package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fields/schedules")
@Tag(name = "Schedules")
public class ScheduleRestController {
    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleRestController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Registers a new schedule")
    public ResponseEntity<Void> createSchedule(
            @Valid @RequestBody ScheduleCreateDTO data
    ) {
        scheduleService.createSchedule(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{fieldName}")
    @Operation(summary = "Get a list of Schedules associated with a certain Field")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByFieldName(@PathVariable String fieldName) {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByFieldName(fieldName);
        return ResponseEntity.ok(schedules);
    }
}
