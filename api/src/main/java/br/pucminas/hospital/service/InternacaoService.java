package br.pucminas.hospital.service;

import br.pucminas.hospital.model.Internacao;
import br.pucminas.hospital.repository.AtendimentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class InternacaoService {

    private final AtendimentoRepository atendimentoRepository;
    private final PacienteService pacienteService;
    private final ProfissionalService profissionalService;
    private final QuartoService quartoService;

    public InternacaoService(AtendimentoRepository atendimentoRepository, PacienteService pacienteService,
                             ProfissionalService profissionalService, QuartoService quartoService) {
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteService = pacienteService;
        this.profissionalService = profissionalService;
        this.quartoService = quartoService;
    }

    public List<Internacao> listar(Boolean ativas) {
        return atendimentoRepository.listar(Internacao.class).stream()
                .filter(i -> ativas == null || i.estaAtiva() == ativas)
                .toList();
    }

    public Internacao buscar(Long id) {
        return atendimentoRepository.findById(id)
                .filter(Internacao.class::isInstance)
                .map(Internacao.class::cast)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internação não encontrada."));
    }

    @Transactional
    public Internacao internar(Long pacienteId, Long profissionalId, Long quartoId, Internacao internacao) {
        boolean jaInternado = listar(true).stream().anyMatch(i -> i.getPaciente().getId().equals(pacienteId));
        if (jaInternado) {
            throw new IllegalStateException("O paciente já possui uma internação ativa.");
        }
        internacao.setPaciente(pacienteService.buscar(pacienteId));
        internacao.setProfissional(profissionalService.buscar(profissionalId));
        internacao.setQuarto(quartoService.buscar(quartoId));
        if (internacao.getHorario() == null) {
            internacao.setHorario(LocalTime.now().withSecond(0).withNano(0));
        }
        internacao.registrarEntrada();
        return atendimentoRepository.save(internacao);
    }

    @Transactional
    public Internacao registrarAlta(Long id, LocalDate dataAlta) {
        Internacao internacao = buscar(id);
        internacao.registrarAlta(dataAlta);
        return internacao;
    }
}
