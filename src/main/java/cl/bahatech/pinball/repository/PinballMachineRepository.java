package cl.bahatech.pinball.repository;

import cl.bahatech.pinball.domain.model.PinballMachine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinballMachineRepository extends JpaRepository<PinballMachine, Long> {

    boolean existsByModelName(String modelName);

}
