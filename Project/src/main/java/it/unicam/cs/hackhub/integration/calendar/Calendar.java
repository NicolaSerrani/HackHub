package it.unicam.cs.hackhub.integration.calendar;

import it.unicam.cs.hackhub.model.entity.Call;

import java.time.LocalDateTime;
import java.util.List;

public interface Calendar {
    List<LocalDateTime> getAvailableTimeSlots();

    String registerCall(Call call);
}
