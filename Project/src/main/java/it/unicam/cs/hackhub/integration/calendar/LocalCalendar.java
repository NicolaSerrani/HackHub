package it.unicam.cs.hackhub.integration.calendar;

import it.unicam.cs.hackhub.model.entity.Call;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LocalCalendar implements Calendar {

    private final List<LocalDateTime> availableTimeSlots = new ArrayList<>();

    public LocalCalendar() {
        LocalDateTime firstSlot = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        availableTimeSlots.add(firstSlot);
        availableTimeSlots.add(firstSlot.plusHours(1));
        availableTimeSlots.add(firstSlot.plusHours(2));
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
