package br.pucminas.hospital.controller;

import br.pucminas.hospital.model.ProcedimentoCirurgico;
import br.pucminas.hospital.model.Profissional;
import br.pucminas.hospital.service.ProfissionalService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ProfissionalService service;

    public ProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    public record ProcedimentoRequest(Long pacienteId, LocalDateTime dataHoraPrevista, LocalDateTime dataHoraEfetiva,
                                      String descricao, String tipoCirurgia, String obsPreOperatorias,
                                      String obsPosOperatorias) {
    }

    @GetMapping
    public List<Profissional> listar(@RequestParam(required = false) String especialidade,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horario) {
        return service.listar(especialidade, data, horario);
    }

    @GetMapping("/{id}")
    public Profissional buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profissional criar(@RequestBody @Valid Profissional profissional) {
        return service.criar(profissional);
    }

    @PutMapping("/{id}")
    public Profissional atualizar(@PathVariable Long id, @RequestBody @Valid Profissional profissional) {
        return service.atualizar(id, profissional);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }

    @GetMapping("/{id}/disponibilidade")
    public boolean disponibilidade(@PathVariable Long id,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horario) {
        return service.estaDisponivel(id, data, horario);
    }

    @GetMapping("/{id}/procedimentos")
    public List<ProcedimentoCirurgico> listarProcedimentos(@PathVariable Long id) {
        return service.listarProcedimentos(id);
    }

    @PostMapping("/{id}/procedimentos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProcedimentoCirurgico registrarProcedimento(@PathVariable Long id, @RequestBody ProcedimentoRequest req) {
        ProcedimentoCirurgico procedimento = new ProcedimentoCirurgico();
        procedimento.setDataHoraPrevista(req.dataHoraPrevista());
        procedimento.setDescricao(req.descricao());
        procedimento.setTipoCirurgia(req.tipoCirurgia());
        procedimento.setObsPreOperatorias(req.obsPreOperatorias());
        procedimento.setObsPosOperatorias(req.obsPosOperatorias());
        return service.registrarProcedimento(id, procedimento, req.pacienteId(), req.dataHoraEfetiva());
    }

    @PatchMapping("/{id}/procedimentos/{procedimentoId}/iniciar")
    public ProcedimentoCirurgico iniciarProcedimento(@PathVariable Long id, @PathVariable Long procedimentoId) {
        return service.iniciarProcedimento(id, procedimentoId);
    }

    @PatchMapping("/{id}/procedimentos/{procedimentoId}/concluir")
    public ProcedimentoCirurgico concluirProcedimento(@PathVariable Long id, @PathVariable Long procedimentoId,
                                                      @RequestBody(required = false) String obsPosOperatorias) {
        return service.concluirProcedimento(id, procedimentoId, obsPosOperatorias);
    }

    @PatchMapping("/{id}/procedimentos/{procedimentoId}/cancelar")
    public ProcedimentoCirurgico cancelarProcedimento(@PathVariable Long id, @PathVariable Long procedimentoId) {
        return service.cancelarProcedimento(id, procedimentoId);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> tratarErro(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
