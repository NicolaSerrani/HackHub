package it.unicam.cs.hackhub.integration.calendar;

import it.unicam.cs.hackhub.model.entity.Call;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LocalCalendar implements Calendar {

    private final List<LocalDateTime> availableTimeSlots = new ArrayList<>();

    public LocalCalendar() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        availableTimeSlots.add(tomorrow.atTime(9, 0));
        availableTimeSlots.add(tomorrow.atTime(11, 0));
        availableTimeSlots.add(tomorrow.atTime(14, 0));
    }

    @Override
    public List<LocalDateTime> getAvailableTimeSlots() {
        return List.copyOf(availableTimeSlots);
    }

    @Override
    public String registerCall(Call call) {
        if (call == null || call.getCallId() == null) {
            throw new IllegalArgumentException("A persisted call is required.");
        }
        availableTimeSlots.remove(call.getDateTime());
        return "https://calendar.local/calls/" + call.getCallId();
    }
}
