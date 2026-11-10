package br.pucminas.hospital.service;

import br.pucminas.hospital.model.Cirurgiao;
import br.pucminas.hospital.model.Especialista;
import br.pucminas.hospital.model.ProcedimentoCirurgico;
import br.pucminas.hospital.model.Profissional;
import br.pucminas.hospital.repository.AtendimentoRepository;
import br.pucminas.hospital.repository.ProfissionalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final PacienteService pacienteService;

    public ProfissionalService(ProfissionalRepository profissionalRepository, AtendimentoRepository atendimentoRepository,
                               PacienteService pacienteService) {
        this.profissionalRepository = profissionalRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteService = pacienteService;
    }

    @Transactional(readOnly = true)
    public List<Profissional> listar(String especialidade, LocalDate data, LocalTime horario) {
        if ((data == null) != (horario == null)) {
            throw new IllegalArgumentException("Para ver a disponibilidade, informe a data e o horário.");
        }
        return profissionalRepository.findAll().stream()
                .filter(p -> especialidade == null || especialidade.isBlank()
                        || p instanceof Especialista e && e.getEspecialidade().equalsIgnoreCase(especialidade.trim()))
                .filter(p -> data == null || p.estaDisponivel(data, horario))
                .toList();
    }

    public Profissional buscar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Informe o profissional.");
        }
        return profissionalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profissional não encontrado."));
    }

    @Transactional
    public Profissional criar(Profissional profissional) {
        if (profissionalRepository.existsByRegProf(profissional.getRegProf())) {
            throw new IllegalArgumentException("Já existe um profissional com este registro.");
        }
        return profissionalRepository.save(profissional);
    }

    @Transactional
    public Profissional atualizar(Long id, Profissional dados) {
        Profissional profissional = buscar(id);
        if (profissional.getClass() != dados.getClass()) {
            throw new IllegalArgumentException("A categoria do profissional não pode ser alterada.");
        }
        if (!profissional.getRegProf().equals(dados.getRegProf()) && profissionalRepository.existsByRegProf(dados.getRegProf())) {
            throw new IllegalArgumentException("Já existe um profissional com este registro.");
        }
        profissional.setNome(dados.getNome());
        profissional.setRegProf(dados.getRegProf());
        profissional.setTelefone(dados.getTelefone());
        profissional.setEmail(dados.getEmail());
        if (profissional instanceof Especialista especialista) {
            especialista.setEspecialidade(((Especialista) dados).getEspecialidade());
        }
        if (profissional instanceof Cirurgiao cirurgiao) {
            cirurgiao.setAreaCirurgica(((Cirurgiao) dados).getAreaCirurgica());
        }
        return profissional;
    }

    @Transactional
    public void remover(Long id) {
        Profissional profissional = buscar(id);
        if (!profissional.getAtendimentos().isEmpty()) {
            throw new IllegalStateException("O profissional possui atendimentos e não pode ser excluído.");
        }
        profissionalRepository.delete(profissional);
    }

    @Transactional(readOnly = true)
    public boolean estaDisponivel(Long id, LocalDate data, LocalTime horario) {
        return buscar(id).estaDisponivel(data, horario);
    }

    @Transactional(readOnly = true)
    public List<ProcedimentoCirurgico> listarProcedimentos(Long cirurgiaoId) {
        return buscarCirurgiao(cirurgiaoId).getProcedimentos();
    }

    @Transactional
    public ProcedimentoCirurgico registrarProcedimento(Long cirurgiaoId, ProcedimentoCirurgico procedimento, Long pacienteId,
                                                       LocalDateTime dataHoraEfetiva) {
        Cirurgiao cirurgiao = buscarCirurgiao(cirurgiaoId);
        procedimento.setPaciente(pacienteService.buscar(pacienteId));
        cirurgiao.registrarProcedimento(procedimento);
        if (dataHoraEfetiva != null) {
            cirurgiao.realizarProcedimento(procedimento);
            procedimento.setDataHoraEfetiva(dataHoraEfetiva);
            procedimento.concluir();
        }
        return atendimentoRepository.save(procedimento);
    }

    @Transactional
    public ProcedimentoCirurgico iniciarProcedimento(Long cirurgiaoId, Long procedimentoId) {
        Cirurgiao cirurgiao = buscarCirurgiao(cirurgiaoId);
        ProcedimentoCirurgico procedimento = buscarProcedimento(cirurgiao, procedimentoId);
        cirurgiao.realizarProcedimento(procedimento);
        return procedimento;
    }

    @Transactional
    public ProcedimentoCirurgico concluirProcedimento(Long cirurgiaoId, Long procedimentoId, String obsPosOperatorias) {
        ProcedimentoCirurgico procedimento = buscarProcedimento(buscarCirurgiao(cirurgiaoId), procedimentoId);
        procedimento.concluir();
        if (obsPosOperatorias != null) {
            procedimento.setObsPosOperatorias(obsPosOperatorias);
        }
        return procedimento;
    }

    @Transactional
    public ProcedimentoCirurgico cancelarProcedimento(Long cirurgiaoId, Long procedimentoId) {
        ProcedimentoCirurgico procedimento = buscarProcedimento(buscarCirurgiao(cirurgiaoId), procedimentoId);
        procedimento.cancelar();
        return procedimento;
    }

    private Cirurgiao buscarCirurgiao(Long id) {
        if (!(buscar(id) instanceof Cirurgiao cirurgiao)) {
            throw new IllegalArgumentException("O profissional informado não é cirurgião.");
        }
        return cirurgiao;
    }

    private ProcedimentoCirurgico buscarProcedimento(Cirurgiao cirurgiao, Long procedimentoId) {
        return cirurgiao.getProcedimentos().stream()
                .filter(p -> p.getId().equals(procedimentoId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Procedimento não encontrado."));
    }
}
