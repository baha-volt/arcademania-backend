package cl.bahatech.pinball.service;

import cl.bahatech.pinball.domain.PinballMachine;

import java.util.List;

public interface PinballMachineService {

    List<PinballMachine> findAll();

    PinballMachine findById(Long id);

    PinballMachine save(PinballMachine pinballMachine);

    PinballMachine update(Long id, PinballMachine pinballMachine);

    void remove(Long id);

}
