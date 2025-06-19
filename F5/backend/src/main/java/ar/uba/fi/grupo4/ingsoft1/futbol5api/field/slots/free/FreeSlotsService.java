package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.free;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.common.exception.ItemNotFoundException;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.FieldRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.Schedule;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules.ScheduleRepository;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlot;
import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.slots.BlockedSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FreeSlotsService {
    private final ScheduleRepository scheduleRepository;
    private final FieldRepository fieldRepository;
    private final BlockedSlotRepository blockedSlotRepository;

    private List<FreeSlotDTO> getFreeSlotsPerDayAndSchedule(LocalDate date, Schedule schedule) {
        List<FreeSlotDTO> freeSlots = new ArrayList<>();
        Field field = schedule.getField();
        List<BlockedSlot> blocked = blockedSlotRepository.findByFieldAndDate(field, date);
        Set<Integer> blockedNumbers = blocked.stream().map(BlockedSlot::getSlotNumber).collect(Collectors.toSet());

        LocalTime opening = schedule.getOpeningTime();
        int duration = schedule.getSlotDurationMinutes();

        for (int i = 0; i < schedule.getNumberOfSlots(); i++) {
            if (!blockedNumbers.contains(i)) {
                LocalTime start = opening.plusMinutes((long) i * duration);
                LocalTime end = start.plusMinutes(duration);
                freeSlots.add(new FreeSlotDTO(field.getName(), date, i, start, end));
            }
        }

        return freeSlots;
    }

    @Autowired
    public FreeSlotsService(ScheduleRepository scheduleRepository, FieldRepository fieldRepository, BlockedSlotRepository blockedSlotRepository) {
        this.scheduleRepository = scheduleRepository;
        this.fieldRepository = fieldRepository;
        this.blockedSlotRepository = blockedSlotRepository;
    }

    public Page<FreeSlotDTO> getFreeSlots(String fieldName, int numberOfDays, Pageable pageable) throws ItemNotFoundException {
        Field field = fieldRepository.findByNameAndEnabled(fieldName, true)
                .orElseThrow(() -> new ItemNotFoundException(fieldName));

        Map<DayOfWeek, Schedule> scheduleMap = scheduleRepository
                .findSchedulesByField(field)
                .stream()
                .collect(Collectors.toMap(Schedule::getDayOfWeek, s -> s));

        if (scheduleMap.isEmpty()) {
            throw new ItemNotFoundException("schedule");
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(numberOfDays);

        List<FreeSlotDTO> freeSlots = new ArrayList<>();

        for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            Schedule schedule = scheduleMap.get(date.getDayOfWeek());
            if (schedule == null) continue;

            freeSlots.addAll(getFreeSlotsPerDayAndSchedule(date, schedule));
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), freeSlots.size());
        List<FreeSlotDTO> pageContent = freeSlots.subList(start, end);

        return new PageImpl<>(pageContent, pageable, freeSlots.size());
    }
}