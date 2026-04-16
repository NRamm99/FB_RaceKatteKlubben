package dk.race.racekatteklubben.presentation.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record UpdateEventRequest(
        String title,
        String description,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime
) {
}
