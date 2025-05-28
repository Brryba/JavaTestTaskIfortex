package com.example.java_ifortex_test_task.repository;

import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    /**
     *  I used device_type - 1 because Session entity uses 'EnumType.ORDINAL' to set enum param, but database stores device type codes.
     *  For example database record with device_type = 1 was seen as Desktop because the entity uses the ordinal rather than field value.
     *  According to the task I am only allowed to change Repository and Service layers
     */
    @Query(value = """
            SELECT sessions.id,\s
                    sessions.started_at_utc,\s
                    sessions.ended_at_utc,\s
                    sessions.device_type - 1 as device_type, \s
                    sessions.user_id FROM sessions \s
            WHERE sessions.device_type = :#{#deviceType.getCode()} \s
            ORDER BY started_at_utc\s
            LIMIT 1""", nativeQuery = true)
    Session getFirstDesktopSession(DeviceType deviceType);

    @Query(value = """
            SELECT sessions.id,\s
                    sessions.started_at_utc,\s
                    sessions.ended_at_utc,\s
                    sessions.device_type - 1 as device_type, \s
                    sessions.user_id FROM sessions \s
            WHERE date_part('year', ended_at_utc) < :#{#endDate.getYear()}
            ORDER BY started_at_utc DESC""", nativeQuery = true)
    List<Session> getSessionsFromActiveUsersEndedBefore2025(LocalDateTime endDate);
}