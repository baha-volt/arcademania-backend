package cl.bahatech.pinball.controller;

import cl.bahatech.pinball.domain.PinballMachine;
import cl.bahatech.pinball.service.PinballMachineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pinballs")
public class PinballMachineController {

    private final PinballMachineService service;


    public PinballMachineController(PinballMachineService service) {
        this.service = service;
    }

    // 1. Obtener todas las máquinas
    @GetMapping
    public ResponseEntity<List<PinballMachine>> getAllPinballs() {
        List<PinballMachine> machines = service.findAll();
        if (machines.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 si la lista está vacía
        }
        return ResponseEntity.ok(machines); // HTTP 200 con la lista de objetos
    }

    // 2. Obtener una máquina por ID
    @GetMapping("/{id}")
    public ResponseEntity<PinballMachine> getPinballById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok) // HTTP 200 si la encuentra
                .orElseGet(() -> ResponseEntity.notFound().build()); // HTTP 404 si el Optional viene vacío
    }

    // 3. Crear una nueva máquina
    @PostMapping
    public ResponseEntity<PinballMachine> createPinball(@RequestBody PinballMachine pinballMachine) {
        PinballMachine createdMachine = service.save(pinballMachine);
        // HTTP 201 Created retornando el recurso con su ID autogenerado
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMachine);
    }

    // 4. Actualizar una máquina existente
    @PutMapping("/{id}")
    public ResponseEntity<PinballMachine> updatePinball(@PathVariable Long id, @RequestBody PinballMachine pinballMachine) {
        return service.update(id, pinballMachine)
                .map(ResponseEntity::ok) // HTTP 200 con el objeto actualizado
                .orElseGet(() -> ResponseEntity.notFound().build()); // HTTP 404 si no existía el registro
    }

    // 5. Eliminar una máquina
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePinball(@PathVariable Long id) {
        service.remove(id); // Si no existe, el Service lanzará NonExistingPinballMachineException
        return ResponseEntity.noContent().build(); // HTTP 204 No Content confirmando la eliminación
    }
}