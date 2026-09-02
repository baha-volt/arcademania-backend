package cl.bahatech.pinball.repository;

import cl.bahatech.pinball.infrastructure.persistence.PinballMachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PinballMachineRepository extends JpaRepository<PinballMachineEntity, Long> {

    boolean existsByModelName(String modelName);

    Optional<PinballMachineEntity> findByModelNameIgnoreCase(String modelName);

}
