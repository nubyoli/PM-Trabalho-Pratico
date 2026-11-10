package br.pucminas.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class AtendimentoEmergencia extends Atendimento {

    @Enumerated(EnumType.STRING)
    private PrioridadeEmergencia prioridade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_emergencia")
    private StatusEmergencia status = StatusEmergencia.AGUARDANDO_AVALIACAO;

    public void iniciar() {
        status = StatusEmergencia.EM_ATENDIMENTO;
    }

    public void concluir() {
        status = StatusEmergencia.CONCLUIDO;
    }

    public boolean estaAguardando() {
        return status == StatusEmergencia.AGUARDANDO_AVALIACAO;
    }

    @Override
    public String getTipo() {
        return "Atendimento de emergência";
    }

    @Override
    public String gerarResumo() {
        return getTipo() + " (prioridade " + prioridade + ") em " + getData() + " às " + getHorario()
                + " com " + getProfissional().getNome() + " (" + status + ")";
    }

    public PrioridadeEmergencia getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeEmergencia prioridade) {
        this.prioridade = prioridade;
    }

    public StatusEmergencia getStatus() {
        return status;
    }
}
