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
    @Transactional
    public PinballMachine save(PinballMachine pinballMachine) {
        if (repository.existsByModelName(pinballMachine.modelName())) {
            throw new DuplicatePinballMachineException(
                    "A pinball machine with model name '" + pinballMachine.modelName() + "' already exists");
        }
        PinballMachineEntity saved = repository.save(toEntity(pinballMachine));
        return toDomain(saved);
    }

    @Override
    @Transactional
    public PinballMachine update(Long id, PinballMachine pinballMachine) {
        PinballMachineEntity existing = findEntityById(id);
        existing.setModelName(pinballMachine.modelName());
        existing.setManufacturer(pinballMachine.manufacturer());
        existing.setRarityTier(pinballMachine.rarityTier());
        existing.setImageUrl(pinballMachine.imageUrl());
        existing.setHistoricalSummary(pinballMachine.historicalSummary());
        existing.setReleaseYear(pinballMachine.releaseYear());
        existing.setUnitsProduced(pinballMachine.unitsProduced());
        existing.setRestorationCostUsd(pinballMachine.restorationCostUsd());
        existing.setConditionRating(pinballMachine.conditionRating());
        existing.setIsFullyFunctional(pinballMachine.isFullyFunctional());
        existing.setHasMultiball(pinballMachine.hasMultiball());
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
                domain.modelName(),
                domain.manufacturer(),
                domain.rarityTier(),
                domain.imageUrl(),
                domain.historicalSummary(),
                domain.releaseYear(),
                domain.unitsProduced(),
                domain.restorationCostUsd(),
                domain.conditionRating(),
                domain.isFullyFunctional(),
                domain.hasMultiball()
        );
    }

}
