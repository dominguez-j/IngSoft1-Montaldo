package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final FieldRepository fieldRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, FieldRepository fieldRepository) {
        this.scheduleRepository = scheduleRepository;
        this.fieldRepository = fieldRepository;
    }

    public void createSchedule(ScheduleCreateDTO dto) throws IllegalArgumentException, DataIntegrityViolationException {
        Field field = fieldRepository.findByName(dto.fieldName())
                .orElseThrow(() -> new IllegalArgumentException("Field not found: " + dto.fieldName()));

        boolean alreadyExists = scheduleRepository.existsByFieldAndDayOfWeek(field, dto.dayOfWeek());
        if (alreadyExists) {
            throw new DataIntegrityViolationException("Schedule already exists for this field and day");
        }

        if (dto.openingTime().isAfter(dto.closingTime()) || dto.openingTime().equals(dto.closingTime())) {
            throw new IllegalArgumentException("Opening time must be before closing time");
        }

        if (dto.slotDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Slot duration must be greater than 0");
        }

        scheduleRepository.save(dto.asSchedule(fieldRepository::findByName));
    }

    public List<ScheduleDTO> getSchedulesByFieldName(String fieldName) {
        List<Schedule> schedules = scheduleRepository.findByField_Name(fieldName);
        return schedules.stream()
                .map(ScheduleDTO::new)
                .toList();
    }
}
