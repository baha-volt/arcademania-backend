package cl.bahatech.pinball.application.service;

import cl.bahatech.pinball.domain.exception.DuplicatePinballMachineException;
import cl.bahatech.pinball.domain.exception.NonExistingPinballMachineException;
import cl.bahatech.pinball.domain.model.PinballMachine;
import cl.bahatech.pinball.infrastructure.persistence.PinballMachineEntity;
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
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PinballMachine findById(Long id) {
        return toDomain(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PinballMachine findByModelName(String modelName) {
        PinballMachineEntity entity = repository.findByModelNameIgnoreCase(modelName)
                .orElseThrow(() -> new NonExistingPinballMachineException(
                        "Pinball machine with model name '" + modelName + "' not found"));
        return toDomain(entity);
    }

    @Override
    @Transactional
    public PinballMachine save(PinballMachine pinballMachine) {
        if (repository.existsByModelName(pinballMachine.getModelName())) {
            throw new DuplicatePinballMachineException(
                    "A pinball machine with model name '" + pinballMachine.getModelName() + "' already exists");
        }
        PinballMachineEntity saved = repository.save(toEntity(pinballMachine));
        return toDomain(saved);
    }

    @Override
    @Transactional
    public PinballMachine update(Long id, PinballMachine pinballMachine) {
        PinballMachineEntity existing = findEntityById(id);
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
        return toDomain(repository.save(existing));
    }

    @Override
    @Transactional
    public void remove(Long id) {
        if (!repository.existsById(id)) {
            throw new NonExistingPinballMachineException("Cannot delete: pinball machine with ID " + id + " not found");
        }
        repository.deleteById(id);
    }

    private PinballMachineEntity findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NonExistingPinballMachineException("Pinball machine with ID " + id + " not found"));
    }

    private PinballMachine toDomain(PinballMachineEntity entity) {
        return new PinballMachine(
                entity.getId(),
                entity.getModelName(),
                entity.getManufacturer(),
                entity.getRarityTier(),
                entity.getImageUrl(),
                entity.getHistoricalSummary(),
                entity.getReleaseYear(),
                entity.getUnitsProduced(),
                entity.getRestorationCostUsd(),
                entity.getConditionRating(),
                entity.getIsFullyFunctional(),
                entity.getHasMultiball()
        );
    }

    private PinballMachineEntity toEntity(PinballMachine domain) {
        return new PinballMachineEntity(
                domain.getModelName(),
                domain.getManufacturer(),
                domain.getRarityTier(),
                domain.getImageUrl(),
                domain.getHistoricalSummary(),
                domain.getReleaseYear(),
                domain.getUnitsProduced(),
                domain.getRestorationCostUsd(),
                domain.getConditionRating(),
                domain.getIsFullyFunctional(),
                domain.getHasMultiball()
        );
    }

}
