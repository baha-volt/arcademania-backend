package cl.bahatech.pinball.application.service;

import cl.bahatech.pinball.domain.exception.DuplicatePinballMachineException;
import cl.bahatech.pinball.domain.exception.NonExistingPinballMachineException;
import cl.bahatech.pinball.domain.model.PinballMachine;
import cl.bahatech.pinball.infrastructure.persistence.PinballMachineEntity;
import cl.bahatech.pinball.repository.PinballMachineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestPinballMachineServiceImpl {

    @Mock
    private PinballMachineRepository repository;

    @InjectMocks
    private PinballMachineServiceImpl service;

    private PinballMachineEntity defaultEntity;

    @BeforeEach
    void setUp() {
        service = new PinballMachineServiceImpl(repository);

        defaultEntity = new PinballMachineEntity(
                "Twilight Zone", "Bally", "Leyenda", "https://test.com/tz.svg",
                "Resumen", 1993, 15235, new BigDecimal("1450.00"), 4.9, true, true);
        defaultEntity.setId(1L);
    }

    @Test
    void shouldCreateServiceSuccessfully() {

        assertNotNull(service);
    }

    @Test
    void shouldFindAllPinballMachines() {

        when(repository.findAll()).thenReturn(List.of(defaultEntity));

        List<PinballMachine> result = service.findAll();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Twilight Zone", result.get(0).getModelName())
        );
        verify(repository).findAll();
    }

    @Test
    void shouldFindPinballMachineById() {

        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(defaultEntity));

        PinballMachine result = service.findById(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("Twilight Zone", result.getModelName())
        );
        verify(repository).findById(id);
    }

    @Test
    void shouldThrowNonExistingPinballMachineExceptionWhenFindByIdNotFound() {

        Long id = 99L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        NonExistingPinballMachineException ex = assertThrows(
                NonExistingPinballMachineException.class,
                () -> service.findById(id));

        assertEquals("Pinball machine with ID 99 not found", ex.getMessage());
        verify(repository).findById(id);
    }

    @Test
    void shouldFindPinballMachineByModelName() {

        String modelName = "Twilight Zone";
        when(repository.findByModelNameIgnoreCase(modelName)).thenReturn(Optional.of(defaultEntity));

        PinballMachine result = service.findByModelName(modelName);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.getId())
        );
        verify(repository).findByModelNameIgnoreCase(modelName);
    }

    @Test
    void shouldThrowNonExistingPinballMachineExceptionWhenFindByModelNameNotFound() {

        String modelName = "Unknown Machine";
        when(repository.findByModelNameIgnoreCase(modelName)).thenReturn(Optional.empty());

        NonExistingPinballMachineException ex = assertThrows(
                NonExistingPinballMachineException.class,
                () -> service.findByModelName(modelName));

        assertEquals("Pinball machine with model name 'Unknown Machine' not found", ex.getMessage());
    }

    @Test
    void shouldSavePinballMachineSuccessfullyWhenModelNameDoesNotExist() {

        PinballMachine newMachine = new PinballMachine(
                null, "Neon Rush 2077", "Cyber Arcade Co.", "Edicion Limitada", null, null,
                2026, 500, null, null, true, true);

        when(repository.existsByModelName("Neon Rush 2077")).thenReturn(false);
        when(repository.save(any(PinballMachineEntity.class))).thenAnswer(invocation -> {
            PinballMachineEntity entity = invocation.getArgument(0);
            entity.setId(2L);
            return entity;
        });

        PinballMachine result = service.save(newMachine);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2L, result.getId()),
                () -> assertEquals("Neon Rush 2077", result.getModelName())
        );
        verify(repository).existsByModelName("Neon Rush 2077");
        verify(repository).save(any(PinballMachineEntity.class));
    }

    @Test
    void shouldThrowDuplicatePinballMachineExceptionWhenModelNameAlreadyExists() {

        PinballMachine duplicated = new PinballMachine(
                null, "Twilight Zone", "Bally", "Leyenda", null, null,
                1993, 15235, null, null, true, true);

        when(repository.existsByModelName("Twilight Zone")).thenReturn(true);

        DuplicatePinballMachineException ex = assertThrows(
                DuplicatePinballMachineException.class,
                () -> service.save(duplicated));

        assertEquals("A pinball machine with model name 'Twilight Zone' already exists", ex.getMessage());
        verify(repository, never()).save(any(PinballMachineEntity.class));
    }

    @Test
    void shouldUpdatePinballMachineSuccessfully() {

        Long id = 1L;
        PinballMachine updateData = new PinballMachine(
                null, "Twilight Zone", "Bally", "Leyenda", "https://test.com/updated.svg",
                "Nuevo resumen", 1993, 16000, new BigDecimal("1500.00"), 5.0, true, true);

        when(repository.findById(id)).thenReturn(Optional.of(defaultEntity));
        when(repository.save(any(PinballMachineEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PinballMachine result = service.update(id, updateData);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(16000, result.getUnitsProduced()),
                () -> assertEquals(new BigDecimal("1500.00"), result.getRestorationCostUsd()),
                () -> assertEquals(5.0, result.getConditionRating())
        );
        verify(repository).findById(id);
        verify(repository).save(any(PinballMachineEntity.class));
    }

    @Test
    void shouldThrowNonExistingPinballMachineExceptionWhenUpdatingNonExistingMachine() {

        Long id = 99L;
        PinballMachine anyMachine = new PinballMachine(
                null, "Any Machine", "Any", null, null, null,
                2000, null, null, null, true, false);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                NonExistingPinballMachineException.class,
                () -> service.update(id, anyMachine));

        verify(repository, never()).save(any(PinballMachineEntity.class));
    }

    @Test
    void shouldRemovePinballMachineSuccessfully() {

        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> service.remove(id));

        verify(repository).existsById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void shouldThrowNonExistingPinballMachineExceptionWhenRemovingNonExistingMachine() {

        Long id = 99L;
        when(repository.existsById(id)).thenReturn(false);

        NonExistingPinballMachineException ex = assertThrows(
                NonExistingPinballMachineException.class,
                () -> service.remove(id));

        assertEquals("Cannot delete: pinball machine with ID 99 not found", ex.getMessage());
        verify(repository, never()).deleteById(anyLong());
    }
}
