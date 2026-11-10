package br.pucminas.hospital.service;

import br.pucminas.hospital.model.Historico;
import br.pucminas.hospital.model.Paciente;
import br.pucminas.hospital.repository.AtendimentoRepository;
import br.pucminas.hospital.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final AtendimentoRepository atendimentoRepository;

    public PacienteService(PacienteRepository pacienteRepository, AtendimentoRepository atendimentoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.atendimentoRepository = atendimentoRepository;
    }

    public List<Paciente> listar() {
        return pacienteRepository.findAll();
    }

    public Paciente buscar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Informe o paciente.");
        }
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado."));
    }

    @Transactional
    public Paciente criar(Paciente paciente) {
        if (paciente.getCpf() == null || paciente.getCpf().isBlank()) {
            throw new IllegalArgumentException("Informe o CPF.");
        }
        if (pacienteRepository.existsByCpf(paciente.getCpf())) {
            throw new IllegalArgumentException("Já existe um paciente com este CPF.");
        }
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente atualizar(Long id, Paciente dados) {
        Paciente paciente = buscar(id);
        if (dados.getCpf() == null || dados.getCpf().isBlank()) {
            throw new IllegalArgumentException("Informe o CPF.");
        }
        if (!paciente.getCpf().equals(dados.getCpf()) && pacienteRepository.existsByCpf(dados.getCpf())) {
            throw new IllegalArgumentException("Já existe um paciente com este CPF.");
        }
        paciente.setNome(dados.getNome());
        paciente.setCpf(dados.getCpf());
        paciente.setDataNasc(dados.getDataNasc());
        paciente.setTelefone(dados.getTelefone());
        paciente.setEmail(dados.getEmail());
        paciente.setEndereco(dados.getEndereco());
        return paciente;
    }

    @Transactional
    public void remover(Long id) {
        Paciente paciente = buscar(id);
        if (!atendimentoRepository.findByPacienteId(id).isEmpty()) {
            throw new IllegalStateException("O paciente possui atendimentos no histórico e não pode ser excluído.");
        }
        pacienteRepository.delete(paciente);
    }

    @Transactional(readOnly = true)
    public Historico historico(Long id) {
        Historico historico = new Historico(buscar(id));
        atendimentoRepository.findByPacienteId(id).forEach(historico::adicionar);
        return historico;
    }
}
