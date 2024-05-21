package com.otg.tech.notification.repository;

import com.otg.tech.notification.domain.entity.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, String> {

    Optional<NotificationEvent> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query(value = """
            update notification_events
                set notification_event_status='PROCESSING'
                where id IN (
                    select id from notification_events e
                    where notification_event_status = 'ENQUEUED'
                    and ((customer_data->>'scheduledDate') IS NULL
                          or (
                            CASE
                              WHEN (customer_data->>'scheduledDate') ~ '^\\d{4}-\\d{2}-\\d{2}' THEN
                                to_timestamp(customer_data->>'scheduledDate', 'YYYY-MM-DD HH24:MI:SS') <= now()
                              else
                              false
                              END
                          ))
                    order by created_at
                    FOR UPDATE SKIP LOCKED
                    limit ?1)
            RETURNING *""", nativeQuery = true)
    List<NotificationEvent> fetchAndUpdateEventsStateToProcessing(int batchSize);
}
