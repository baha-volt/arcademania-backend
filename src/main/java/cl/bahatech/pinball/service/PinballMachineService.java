package cl.bahatech.pinball.service;

import cl.bahatech.pinball.domain.PinballMachine;

import java.util.List;
import java.util.Optional;

public interface PinballMachineService {
    List<PinballMachine> findAll();
    Optional<PinballMachine> findById(Long id);
    PinballMachine save(PinballMachine pinballMachine);
    Optional<PinballMachine> update(Long id, PinballMachine pinballMachine);
    void remove(Long id);
}
