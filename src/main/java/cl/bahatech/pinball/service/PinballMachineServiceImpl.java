package cl.bahatech.pinball.service;

import cl.bahatech.pinball.domain.PinballMachine;
import cl.bahatech.pinball.exception.DuplicatePinballMachineException;
import cl.bahatech.pinball.exception.NonExistingPinballMachineException;
import cl.bahatech.pinball.repository.PinballMachineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PinballMachineServiceImpl implements PinballMachineService{

    @Autowired
    private PinballMachineRepository repo;


    private PinballMachine findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NonExistingPinballMachineException("Pinball machine with ID " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PinballMachine> findAll() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PinballMachine> findById(Long id) {
        return repo.findById(id); //Throws 404 directly
    }

    @Override
    @Transactional
    public PinballMachine save(PinballMachine pinballMachine) {
        if (repo.existsByModelName(pinballMachine.getModelName())) {
            throw new DuplicatePinballMachineException(
                    "Ya existe una máquina de pinball registrada con el nombre: " + pinballMachine.getModelName()
            );
        }
        return repo.save(pinballMachine);
    }

    @Override
    @Transactional
    public Optional<PinballMachine> update(Long id, PinballMachine pinballMachine) {
        return repo.findById(id).map(existingPinball -> {
            existingPinball.setModelName(pinballMachine.getModelName());
            existingPinball.setManufacturer(pinballMachine.getManufacturer());
            existingPinball.setRarityTier(pinballMachine.getRarityTier());
            existingPinball.setImageUrl(pinballMachine.getImageUrl());
            existingPinball.setHistoricalSummary(pinballMachine.getHistoricalSummary());
            existingPinball.setReleaseYear(pinballMachine.getReleaseYear());
            existingPinball.setUnitsProduced(pinballMachine.getUnitsProduced());
            existingPinball.setRestorationCostUsd(pinballMachine.getRestorationCostUsd());
            existingPinball.setConditionRating(pinballMachine.getConditionRating());
            existingPinball.setIsFullyFunctional(pinballMachine.getIsFullyFunctional());
            existingPinball.setHasMultiball(pinballMachine.getHasMultiball());

            return repo.save(existingPinball);
        });
    }

    @Override
    @Transactional
    public void remove(Long id) {
        if (!repo.existsById(id)) {
            throw new NonExistingPinballMachineException("Cannot delete: Pinball machine with ID " + id + " not found");
        }
        repo.deleteById(id);
    }
}