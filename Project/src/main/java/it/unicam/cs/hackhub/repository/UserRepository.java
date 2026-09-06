package it.unicam.cs.hackhub.repository;

import it.unicam.cs.hackhub.model.entity.Judge;
import it.unicam.cs.hackhub.model.entity.Mentor;
import it.unicam.cs.hackhub.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    @Query("select m from Mentor m")
    List<Mentor> findAllMentors();

    @Query("select j from Judge j")
    List<Judge> findAllJudges();
}
