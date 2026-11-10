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

    @Override
    public boolean validarCondicoes() {
        super.validarCondicoes();
        if (prioridade == null) {
            throw new IllegalStateException("Informe a prioridade: BAIXA, MODERADA, ALTA ou CRITICA.");
        }
        return true;
    }

    public void iniciar() {
        if (!estaAguardando()) {
            throw new IllegalStateException("Só é possível iniciar um atendimento que aguarda avaliação.");
        }
        status = StatusEmergencia.EM_ATENDIMENTO;
    }

    public void concluir() {
        if (status != StatusEmergencia.EM_ATENDIMENTO) {
            throw new IllegalStateException("Só é possível concluir um atendimento que já foi iniciado.");
        }
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
