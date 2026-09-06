package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Call;

import java.util.List;

public interface CallRepository {
    void save(Call call);
    Call findById(Long id);
    List<Call> findByTeam(Long teamId);
}
