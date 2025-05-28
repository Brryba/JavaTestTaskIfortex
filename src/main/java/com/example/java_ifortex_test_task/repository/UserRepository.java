package com.example.java_ifortex_test_task.repository;

import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
            SELECT users.* FROM public.users
            JOIN (SELECT max(started_at_utc) min_date, user_id
               FROM public.sessions
               WHERE sessions.device_type = :#{#deviceType.getCode()}
               GROUP BY user_id) start_dates
            ON users.id = start_dates.user_id
            ORDER BY start_dates.min_date DESC""", nativeQuery = true)
    List<User> getUsersWithAtLeastOneMobileSession(DeviceType deviceType);

    @Query(value = """
            SELECT users.* FROM public.sessions
            JOIN public.users on sessions.user_id = users.id
            GROUP BY users.id
            ORDER BY count(sessions.id) DESC
            LIMIT 1""", nativeQuery = true)
    User getUserWithMostSessions();
}
