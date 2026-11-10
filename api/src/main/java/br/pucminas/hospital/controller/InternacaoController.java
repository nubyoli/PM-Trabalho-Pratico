package br.pucminas.hospital.controller;

import br.pucminas.hospital.model.Internacao;
import br.pucminas.hospital.service.InternacaoService;
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
@RequestMapping("/internacoes")
public class InternacaoController {

    private final InternacaoService service;

    public InternacaoController(InternacaoService service) {
        this.service = service;
    }

    public record InternacaoRequest(Long pacienteId, Long profissionalId, Long quartoId, LocalDate dataEntrada,
                                    LocalTime horario, LocalDate dataPrevistaAlta, String observacoes) {
    }

    public record AltaRequest(LocalDate dataAlta) {
    }

    @GetMapping
    public List<Internacao> listar(@RequestParam(required = false) Boolean ativas) {
        return service.listar(ativas);
    }

    @GetMapping("/{id}")
    public Internacao buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Internacao internar(@RequestBody InternacaoRequest req) {
        Internacao internacao = new Internacao();
        internacao.setDataEntrada(req.dataEntrada());
        internacao.setHorario(req.horario());
        internacao.setDataPrevistaAlta(req.dataPrevistaAlta());
        internacao.setObservacoes(req.observacoes());
        return service.internar(req.pacienteId(), req.profissionalId(), req.quartoId(), internacao);
    }

    @PatchMapping("/{id}/alta")
    public Internacao registrarAlta(@PathVariable Long id, @RequestBody AltaRequest req) {
        return service.registrarAlta(id, req.dataAlta());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> tratarErro(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
