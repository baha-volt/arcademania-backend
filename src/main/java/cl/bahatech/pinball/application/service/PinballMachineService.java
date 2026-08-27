package cl.bahatech.pinball.application.service;

import cl.bahatech.pinball.domain.model.PinballMachine;

import java.util.List;

public interface PinballMachineService {

    List<PinballMachine> findAll();

    PinballMachine findById(Long id);

    PinballMachine save(PinballMachine pinballMachine);

    PinballMachine update(Long id, PinballMachine pinballMachine);

    void remove(Long id);

}
