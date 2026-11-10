package br.pucminas.hospital.model;

import jakarta.persistence.Entity;

@Entity
public class ClinicoGeral extends Profissional {

    public void realizarConsulta(ConsultaRotina consulta) {
        consulta.iniciar();
    }

    public void registrarObservacoes(ConsultaRotina consulta, String observacoes) {
        consulta.setObservacoes(observacoes);
    }

    public void encaminhar(Paciente paciente, String especialidade) {
        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException("Informe a especialidade do encaminhamento.");
        }
        ConsultaRotina consulta = getAtendimentos().stream()
                .filter(a -> a instanceof ConsultaRotina c
                        && c.getStatus() == StatusConsulta.EM_ATENDIMENTO
                        && c.getPaciente().getId().equals(paciente.getId()))
                .map(ConsultaRotina.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "O paciente precisa estar em uma consulta de rotina em atendimento com " + getNome() + "."));
        String encaminhamento = "Encaminhado(a) para " + especialidade + ".";
        String observacoes = consulta.getObservacoes();
        consulta.setObservacoes(observacoes == null || observacoes.isBlank() ? encaminhamento : observacoes + " " + encaminhamento);
    }
}
