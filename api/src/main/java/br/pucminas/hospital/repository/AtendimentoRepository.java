package br.pucminas.hospital.repository;

import br.pucminas.hospital.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    List<Atendimento> findByPacienteId(Long pacienteId);

    default <T extends Atendimento> List<T> listar(Class<T> tipo) {
        return findAll().stream().filter(tipo::isInstance).map(tipo::cast).toList();
    }
}
