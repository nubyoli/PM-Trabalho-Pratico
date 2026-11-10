package br.pucminas.hospital.controller;

import br.pucminas.hospital.model.AtendimentoEmergencia;
import br.pucminas.hospital.model.Consulta;
import br.pucminas.hospital.model.ConsultaEspecializada;
import br.pucminas.hospital.model.ConsultaRotina;
import br.pucminas.hospital.model.PrioridadeEmergencia;
import br.pucminas.hospital.model.StatusConsulta;
import br.pucminas.hospital.model.StatusEmergencia;
import br.pucminas.hospital.service.ConsultaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    public record ConsultaRequest(String tipo, Long pacienteId, Long profissionalId, LocalDate data,
                                  LocalTime horario, String motivo, String especialidade) {
    }

    public record EmergenciaRequest(Long pacienteId, Long profissionalId, PrioridadeEmergencia prioridade,
                                    String observacoes) {
    }

    @GetMapping
    public List<Consulta> listar(@RequestParam(required = false) StatusConsulta status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    public Consulta buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta agendar(@RequestBody ConsultaRequest req) {
        Consulta consulta;
        if ("ROTINA".equalsIgnoreCase(req.tipo())) {
            consulta = new ConsultaRotina();
        } else if ("ESPECIALIZADA".equalsIgnoreCase(req.tipo())) {
            ConsultaEspecializada especializada = new ConsultaEspecializada();
            especializada.setEspecialidade(req.especialidade());
            consulta = especializada;
        } else {
            throw new IllegalArgumentException("O tipo da consulta deve ser ROTINA ou ESPECIALIZADA.");
        }
        consulta.setData(req.data());
        consulta.setHorario(req.horario());
        consulta.setMotivo(req.motivo());
        return service.agendar(consulta, req.pacienteId(), req.profissionalId());
    }

    @PatchMapping("/{id}/confirmar")
    public Consulta confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @PatchMapping("/{id}/iniciar")
    public Consulta iniciar(@PathVariable Long id) {
        return service.iniciar(id);
    }

    @PatchMapping("/{id}/observacoes")
    public Consulta registrarObservacoes(@PathVariable Long id, @RequestBody String observacoes) {
        return service.registrarObservacoes(id, observacoes);
    }

    @PatchMapping("/{id}/concluir")
    public Consulta concluir(@PathVariable Long id, @RequestBody(required = false) String observacoes) {
        return service.concluir(id, observacoes);
    }

    @PatchMapping("/{id}/cancelar")
    public Consulta cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PatchMapping("/{id}/nao-compareceu")
    public Consulta marcarNaoCompareceu(@PathVariable Long id) {
        return service.marcarNaoCompareceu(id);
    }

    @PatchMapping("/{id}/encaminhar")
    public Consulta encaminhar(@PathVariable Long id, @RequestParam String especialidade) {
        return service.encaminhar(id, especialidade);
    }

    @GetMapping("/emergencias")
    public List<AtendimentoEmergencia> listarEmergencias(@RequestParam(required = false) StatusEmergencia status) {
        return service.listarEmergencias(status);
    }

    @PostMapping("/emergencias")
    @ResponseStatus(HttpStatus.CREATED)
    public AtendimentoEmergencia registrarEmergencia(@RequestBody EmergenciaRequest req) {
        AtendimentoEmergencia emergencia = new AtendimentoEmergencia();
        emergencia.setPrioridade(req.prioridade());
        emergencia.setObservacoes(req.observacoes());
        return service.registrarEmergencia(emergencia, req.pacienteId(), req.profissionalId());
    }

    @PatchMapping("/emergencias/{id}/iniciar")
    public AtendimentoEmergencia iniciarEmergencia(@PathVariable Long id) {
        return service.iniciarEmergencia(id);
    }

    @PatchMapping("/emergencias/{id}/concluir")
    public AtendimentoEmergencia concluirEmergencia(@PathVariable Long id, @RequestBody(required = false) String observacoes) {
        return service.concluirEmergencia(id, observacoes);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> tratarErro(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
