package cl.bahatech.pinball.service;

import cl.bahatech.pinball.domain.PinballMachine;
import cl.bahatech.pinball.exception.DuplicatePinballMachineException;
import cl.bahatech.pinball.exception.NonExistingPinballMachineException;
import cl.bahatech.pinball.repository.PinballMachineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PinballMachineServiceImpl implements PinballMachineService {

    private final PinballMachineRepository repository;

    public PinballMachineServiceImpl(PinballMachineRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PinballMachine> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PinballMachine findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NonExistingPinballMachineException("Pinball machine with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public PinballMachine save(PinballMachine pinballMachine) {
        if (repository.existsByModelName(pinballMachine.getModelName())) {
            throw new DuplicatePinballMachineException(
                    "A pinball machine with model name '" + pinballMachine.getModelName() + "' already exists");
        }
        return repository.save(pinballMachine);
    }

    @Override
    @Transactional
    public PinballMachine update(Long id, PinballMachine pinballMachine) {
        PinballMachine existing = findById(id);
        existing.setModelName(pinballMachine.getModelName());
        existing.setManufacturer(pinballMachine.getManufacturer());
        existing.setRarityTier(pinballMachine.getRarityTier());
        existing.setImageUrl(pinballMachine.getImageUrl());
        existing.setHistoricalSummary(pinballMachine.getHistoricalSummary());
        existing.setReleaseYear(pinballMachine.getReleaseYear());
        existing.setUnitsProduced(pinballMachine.getUnitsProduced());
        existing.setRestorationCostUsd(pinballMachine.getRestorationCostUsd());
        existing.setConditionRating(pinballMachine.getConditionRating());
        existing.setIsFullyFunctional(pinballMachine.getIsFullyFunctional());
        existing.setHasMultiball(pinballMachine.getHasMultiball());
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        if (!repository.existsById(id)) {
            throw new NonExistingPinballMachineException("Cannot delete: pinball machine with ID " + id + " not found");
        }
        repository.deleteById(id);
    }

}
