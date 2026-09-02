package cl.bahatech.pinball.infrastructure.web.controller;

import cl.bahatech.pinball.domain.model.PinballMachine;
import cl.bahatech.pinball.infrastructure.web.dto.PinballMachineRequestDto;
import cl.bahatech.pinball.infrastructure.web.dto.PinballMachineResponseDto;
import cl.bahatech.pinball.application.service.PinballMachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pinballs")
@Tag(name = "Maquinas de Pinball", description = "Operaciones sobre el catalogo de maquinas de pinball vintage")
public class PinballMachineController {

    private final PinballMachineService service;

    public PinballMachineController(PinballMachineService service) {
        this.service = service;
    }

    @Operation(summary = "Listar maquinas de pinball", description = "Devuelve todas las maquinas de pinball registradas en el catalogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Catalogo obtenido exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay maquinas registradas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<PinballMachineResponseDto>> getAllPinballs() {
        List<PinballMachineResponseDto> machines = service.findAll().stream()
                .map(this::toResponse)
                .toList();
        if (machines.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(machines);
    }

    @Operation(summary = "Obtener maquina por ID", description = "Devuelve una maquina de pinball a partir de su identificador numerico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Maquina encontrada"),
        @ApiResponse(responseCode = "404", description = "Maquina no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PinballMachineResponseDto> getPinballById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(service.findById(id)));
    }

    @Operation(summary = "Buscar maquina por nombre de modelo", description = "Devuelve una maquina de pinball a partir de su nombre de modelo exacto (no distingue mayusculas/minusculas)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Maquina encontrada"),
        @ApiResponse(responseCode = "404", description = "No existe una maquina con ese nombre de modelo"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/search")
    public ResponseEntity<PinballMachineResponseDto> getPinballByModelName(
            @Parameter(description = "Nombre de modelo exacto a buscar", example = "Twilight Zone")
            @RequestParam String modelName) {
        return ResponseEntity.ok(toResponse(service.findByModelName(modelName)));
    }

    @Operation(summary = "Registrar maquina de pinball", description = "Crea una nueva maquina de pinball en el catalogo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Maquina creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe una maquina con el mismo nombre de modelo"),
        @ApiResponse(responseCode = "422", description = "Violacion de una regla de negocio del dominio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<PinballMachineResponseDto> createPinball(@Valid @RequestBody PinballMachineRequestDto request) {
        PinballMachine created = service.save(toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @Operation(summary = "Actualizar maquina de pinball", description = "Actualiza los datos de una maquina de pinball existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Maquina actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "404", description = "Maquina no encontrada"),
        @ApiResponse(responseCode = "422", description = "Violacion de una regla de negocio del dominio"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PinballMachineResponseDto> updatePinball(
            @PathVariable Long id,
            @Valid @RequestBody PinballMachineRequestDto request) {
        PinballMachine updated = service.update(id, toDomain(request));
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "Eliminar maquina de pinball", description = "Elimina una maquina de pinball del catalogo por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Maquina eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Maquina no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePinball(@PathVariable Long id) {
        service.remove(id);
        return ResponseEntity.noContent().build();
    }

    private PinballMachine toDomain(PinballMachineRequestDto dto) {
        return new PinballMachine(
                null,
                dto.modelName(),
                dto.manufacturer(),
                dto.rarityTier(),
                dto.imageUrl(),
                dto.historicalSummary(),
                dto.releaseYear(),
                dto.unitsProduced(),
                dto.restorationCostUsd(),
                dto.conditionRating(),
                dto.isFullyFunctional(),
                dto.hasMultiball()
        );
    }

    private PinballMachineResponseDto toResponse(PinballMachine domain) {
        return new PinballMachineResponseDto(
                domain.getId(),
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
