package cl.bahatech.pinball.infrastructure.web.controller;

import cl.bahatech.pinball.application.service.PinballMachineService;
import cl.bahatech.pinball.domain.model.PinballMachine;
import cl.bahatech.pinball.infrastructure.web.dto.PinballMachineRequestDto;
import cl.bahatech.pinball.infrastructure.web.dto.PinballMachineResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestPinballMachineController {

    @Mock
    private PinballMachineService service;

    @InjectMocks
    private PinballMachineController controller;

    private PinballMachine defaultMachine;

    @BeforeEach
    void setUp() {
        controller = new PinballMachineController(service);

        defaultMachine = new PinballMachine(
                1L, "Twilight Zone", "Bally", "Leyenda", "https://test.com/tz.svg",
                "Resumen", 1993, 15235, new BigDecimal("1450.00"), 4.9, true, true);
    }

    @Test
    void shouldReturnOkWithMachinesWhenCatalogIsNotEmpty() {

        when(service.findAll()).thenReturn(List.of(defaultMachine));

        ResponseEntity<List<PinballMachineResponseDto>> response = controller.getAllPinballs();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(1, response.getBody().size()),
                () -> assertEquals("Twilight Zone", response.getBody().get(0).modelName())
        );
    }

    @Test
    void shouldReturnNoContentWhenCatalogIsEmpty() {

        when(service.findAll()).thenReturn(List.of());

        ResponseEntity<List<PinballMachineResponseDto>> response = controller.getAllPinballs();

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertEquals(null, response.getBody())
        );
    }

    @Test
    void shouldReturnOkWithMachineWhenGetPinballById() {

        when(service.findById(1L)).thenReturn(defaultMachine);

        ResponseEntity<PinballMachineResponseDto> response = controller.getPinballById(1L);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(1L, response.getBody().id())
        );
        verify(service).findById(1L);
    }

    @Test
    void shouldReturnOkWithMachineWhenGetPinballByModelName() {

        when(service.findByModelName("Twilight Zone")).thenReturn(defaultMachine);

        ResponseEntity<PinballMachineResponseDto> response = controller.getPinballByModelName("Twilight Zone");

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Twilight Zone", response.getBody().modelName())
        );
        verify(service).findByModelName("Twilight Zone");
    }

    @Test
    void shouldReturnCreatedWhenCreatingPinball() {

        PinballMachineRequestDto request = new PinballMachineRequestDto(
                "Neon Rush 2077", "Cyber Arcade Co.", "Edicion Limitada", null, null,
                2026, 500, null, null, true, true);

        PinballMachine created = new PinballMachine(
                2L, "Neon Rush 2077", "Cyber Arcade Co.", "Edicion Limitada", null, null,
                2026, 500, null, null, true, true);

        when(service.save(any(PinballMachine.class))).thenReturn(created);

        ResponseEntity<PinballMachineResponseDto> response = controller.createPinball(request);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(2L, response.getBody().id()),
                () -> assertTrue(response.getBody().isFullyFunctional())
        );
    }

    @Test
    void shouldReturnOkWhenUpdatingPinball() {

        PinballMachineRequestDto request = new PinballMachineRequestDto(
                "Twilight Zone", "Bally", "Leyenda", null, null,
                1993, 16000, null, null, true, true);

        PinballMachine updated = new PinballMachine(
                1L, "Twilight Zone", "Bally", "Leyenda", null, null,
                1993, 16000, null, null, true, true);

        when(service.update(any(Long.class), any(PinballMachine.class))).thenReturn(updated);

        ResponseEntity<PinballMachineResponseDto> response = controller.updatePinball(1L, request);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(16000, response.getBody().unitsProduced())
        );
    }

    @Test
    void shouldReturnNoContentWhenDeletingPinball() {

        ResponseEntity<Void> response = controller.deletePinball(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).remove(1L);
    }
}
