package br.pucminas.hospital.service;

import br.pucminas.hospital.model.Atendimento;
import br.pucminas.hospital.model.AtendimentoEmergencia;
import br.pucminas.hospital.model.ClinicoGeral;
import br.pucminas.hospital.model.Consulta;
import br.pucminas.hospital.model.ConsultaEspecializada;
import br.pucminas.hospital.model.ConsultaRotina;
import br.pucminas.hospital.model.Especialista;
import br.pucminas.hospital.model.StatusConsulta;
import br.pucminas.hospital.model.StatusEmergencia;
import br.pucminas.hospital.repository.AtendimentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ConsultaService {

    private final AtendimentoRepository atendimentoRepository;
    private final PacienteService pacienteService;
    private final ProfissionalService profissionalService;

    public ConsultaService(AtendimentoRepository atendimentoRepository, PacienteService pacienteService,
                           ProfissionalService profissionalService) {
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteService = pacienteService;
        this.profissionalService = profissionalService;
    }

    public List<Consulta> listar(StatusConsulta status) {
        return atendimentoRepository.listar(Consulta.class).stream()
                .filter(c -> status == null || c.getStatus() == status)
                .toList();
    }

    public Consulta buscar(Long id) {
        return buscar(id, Consulta.class, "Consulta não encontrada.");
    }

    @Transactional
    public Consulta agendar(Consulta consulta, Long pacienteId, Long profissionalId) {
        preencher(consulta, pacienteId, profissionalId);
        consulta.validarCondicoes();
        if (!consulta.getProfissional().estaDisponivel(consulta.getData(), consulta.getHorario())) {
            throw new IllegalStateException(consulta.getProfissional().getNome() + " já possui um atendimento nesse horário.");
        }
        return atendimentoRepository.save(consulta);
    }

    @Transactional
    public Consulta confirmar(Long id) {
        Consulta consulta = buscar(id);
        consulta.confirmar();
        return consulta;
    }

    @Transactional
    public Consulta iniciar(Long id) {
        Consulta consulta = buscar(id);
        consulta.validarCondicoes();
        if (consulta instanceof ConsultaRotina rotina) {
            ((ClinicoGeral) consulta.getProfissional()).realizarConsulta(rotina);
        } else {
            ((Especialista) consulta.getProfissional()).realizarConsulta((ConsultaEspecializada) consulta);
        }
        return consulta;
    }

    @Transactional
    public Consulta registrarObservacoes(Long id, String observacoes) {
        Consulta consulta = buscar(id);
        consulta.validarCondicoes();
        if (consulta instanceof ConsultaRotina rotina) {
            ((ClinicoGeral) consulta.getProfissional()).registrarObservacoes(rotina, observacoes);
        } else {
            ((Especialista) consulta.getProfissional()).registrarObservacoes((ConsultaEspecializada) consulta, observacoes);
        }
        return consulta;
    }

    @Transactional
    public Consulta concluir(Long id, String observacoes) {
        Consulta consulta = buscar(id);
        consulta.concluir(observacoes);
        return consulta;
    }

    @Transactional
    public Consulta cancelar(Long id) {
        Consulta consulta = buscar(id);
        consulta.cancelar();
        return consulta;
    }

    @Transactional
    public Consulta marcarNaoCompareceu(Long id) {
        Consulta consulta = buscar(id);
        consulta.marcarNaoCompareceu();
        return consulta;
    }

    @Transactional
    public Consulta encaminhar(Long id, String especialidade) {
        if (!(buscar(id) instanceof ConsultaRotina consulta)) {
            throw new IllegalArgumentException("Apenas consultas de rotina geram encaminhamento.");
        }
        ((ClinicoGeral) consulta.getProfissional()).encaminhar(consulta.getPaciente(), especialidade);
        return consulta;
    }

    public List<AtendimentoEmergencia> listarEmergencias(StatusEmergencia status) {
        return atendimentoRepository.listar(AtendimentoEmergencia.class).stream()
                .filter(e -> status == null || e.getStatus() == status)
                .sorted(Comparator.comparing(AtendimentoEmergencia::getPrioridade).reversed()
                        .thenComparing(Atendimento::getData)
                        .thenComparing(Atendimento::getHorario))
                .toList();
    }

    @Transactional
    public AtendimentoEmergencia registrarEmergencia(AtendimentoEmergencia emergencia, Long pacienteId, Long profissionalId) {
        preencher(emergencia, pacienteId, profissionalId);
        emergencia.setData(LocalDate.now());
        emergencia.setHorario(LocalTime.now().withSecond(0).withNano(0));
        emergencia.validarCondicoes();
        return atendimentoRepository.save(emergencia);
    }

    @Transactional
    public AtendimentoEmergencia iniciarEmergencia(Long id) {
        AtendimentoEmergencia emergencia = buscar(id, AtendimentoEmergencia.class, "Atendimento de emergência não encontrado.");
        emergencia.iniciar();
        return emergencia;
    }

    @Transactional
    public AtendimentoEmergencia concluirEmergencia(Long id, String observacoes) {
        AtendimentoEmergencia emergencia = buscar(id, AtendimentoEmergencia.class, "Atendimento de emergência não encontrado.");
        emergencia.concluir();
        if (observacoes != null) {
            emergencia.setObservacoes(observacoes);
        }
        return emergencia;
    }

    private void preencher(Atendimento atendimento, Long pacienteId, Long profissionalId) {
        atendimento.setPaciente(pacienteService.buscar(pacienteId));
        atendimento.setProfissional(profissionalService.buscar(profissionalId));
    }

    private <T extends Atendimento> T buscar(Long id, Class<T> tipo, String mensagem) {
        return atendimentoRepository.findById(id)
                .filter(tipo::isInstance)
                .map(tipo::cast)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem));
    }
}
