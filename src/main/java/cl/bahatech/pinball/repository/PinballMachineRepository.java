package cl.bahatech.pinball.repository;

import cl.bahatech.pinball.infrastructure.persistence.PinballMachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinballMachineRepository extends JpaRepository<PinballMachineEntity, Long> {

    boolean existsByModelName(String modelName);

}
