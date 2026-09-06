package it.unicam.cs.hackhub.repository.memory;

import it.unicam.cs.hackhub.model.entity.Call;
import it.unicam.cs.hackhub.repository.CallRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCallRepository implements CallRepository {

    private final Map<Long, Call> calls = new LinkedHashMap<>();

    @Override
    public void save(Call call) {
        if (call == null || call.getCallId() == null) {
            throw new IllegalArgumentException("A call with an ID is required.");
        }
        calls.put(call.getCallId(), call);
    }

    @Override
    public Call findById(Long id) {
        return calls.get(id);
    }

    @Override
    public List<Call> findByTeam(Long teamId) {
        return calls.values().stream()
                .filter(call -> call.getTeam() != null)
                .filter(call -> teamId != null && teamId.equals(call.getTeam().getTeamId()))
                .toList();
    }
}
