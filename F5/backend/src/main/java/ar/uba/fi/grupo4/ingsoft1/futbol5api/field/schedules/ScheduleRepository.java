package ar.uba.fi.grupo4.ingsoft1.futbol5api.field.schedules;

import ar.uba.fi.grupo4.ingsoft1.futbol5api.field.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findScheduleByFieldAndDayOfWeek(Field field, DayOfWeek dayOfWeek);
    List<Schedule> findByField_Name(String fieldName);
    List<Schedule> findSchedulesByField(Field field);
    Boolean existsByFieldAndDayOfWeek(Field field, DayOfWeek dayOfWeek);
}
