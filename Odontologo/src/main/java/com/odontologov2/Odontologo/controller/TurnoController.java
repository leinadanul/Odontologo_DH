package com.odontologov2.Odontologo.controller;


import com.odontologov2.Odontologo.DTO.TurnoDTO;
import com.odontologov2.Odontologo.Exceptions.ResourceNotFoundException;
import com.odontologov2.Odontologo.service.OdontologoService;
import com.odontologov2.Odontologo.service.PacienteService;
import com.odontologov2.Odontologo.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/turnos")
public class TurnoController {

    private TurnoService turnoService;
    private PacienteService pacienteService;
    private OdontologoService odontologoService;

    @Autowired
    public TurnoController(TurnoService turnoService, PacienteService pacienteService, OdontologoService odontologoService) {
        this.turnoService = turnoService;
        this.pacienteService = pacienteService;
        this.odontologoService = odontologoService;
    }
    @GetMapping
    public ResponseEntity<List<TurnoDTO>> buscarTurnos(){
        return ResponseEntity.ok(turnoService.listarTurnos());
    }
    @PostMapping
    public ResponseEntity<TurnoDTO> registrarTurno(@RequestBody TurnoDTO turno){
        //si el odontologo o paciente no existe error
        //PacienteService pacienteService= new PacienteService();
        if(odontologoService.buscarOdontologo(turno.getOdontologoId()).isPresent()
                &&pacienteService.buscarPaciente(turno.getPacienteId()).isPresent()){
            //ambos existen, puedo guardar el turno
            return ResponseEntity.ok(turnoService.guardarTurno(turno));
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<TurnoDTO> buscarTurno(@PathVariable Long id){
        Optional<TurnoDTO> turnoBuscado=turnoService.buscarTurno(id);
        if (turnoBuscado.isPresent()){
            //que el turno existe
            return ResponseEntity.ok(turnoBuscado.get());
        }
        else{
            //no existe el turno con dicho id
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> borrarTurno(@PathVariable Long id) throws ResourceNotFoundException {
        Optional<TurnoDTO> turnoBuscado=turnoService.buscarTurno(id);
        if (turnoBuscado.isPresent()){
            turnoService.eliminarTurno(id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Se eliminó el turno" +
                    " con id= "+id);
        }
        else {
            throw new ResourceNotFoundException("No se puede eliminar el turno" +
                    " con id= "+id);
            //return ResponseEntity.badRequest().body("No se puede eliminar el turno" +
            //        " con id= "+id);
        }
    }
    @PutMapping()
    public ResponseEntity<String> actualizarTurno(@RequestBody TurnoDTO turno){

        //Podemos encontrar un problema con el id del turno
        //Podemos encontrar un problema con el id del odontologo y/o paciente

        Optional<TurnoDTO> turnoBuscado=turnoService.buscarTurno(turno.getId());
        if (turnoBuscado.isPresent()){
            //el turno existe, podemos verificar el resto
            //PacienteService pacienteService= new PacienteService();
            if(odontologoService.buscarOdontologo(turno.getOdontologoId()).isPresent()
                    &&pacienteService.buscarPaciente(turno.getPacienteId()).isPresent()){
                //ambos existen, puedo guardar el turno
                turnoService.actualizarTurno(turno);
                return ResponseEntity.ok("Se actualizó el turno con id= "+turno.getId());
            }
            else{
                return ResponseEntity.badRequest().body("No se puede actualizar el turno con" +
                        " id= "+turno.getId()+" ya que existe un error con el odontologo y/o " +
                        "paciente");
            }
        }
        else {
            //no existe el turno
            return ResponseEntity.badRequest().body("No encontramos el turnos que " +
                    "se quiere modificar.");
        }

    }


}
