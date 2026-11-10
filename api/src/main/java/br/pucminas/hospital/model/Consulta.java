package br.pucminas.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public abstract class Consulta extends Atendimento {

    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_consulta")
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @Override
    public boolean validarCondicoes() {
        super.validarCondicoes();
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalStateException("Informe o motivo da consulta.");
        }
        return true;
    }

    public void confirmar() {
        mudarStatus(StatusConsulta.CONFIRMADA);
    }

    public void iniciar() {
        mudarStatus(StatusConsulta.EM_ATENDIMENTO);
    }

    public void concluir(String observacoes) {
        mudarStatus(StatusConsulta.CONCLUIDA);
        if (observacoes != null && !observacoes.isBlank()) {
            setObservacoes(observacoes);
        }
    }

    public void cancelar() {
        mudarStatus(StatusConsulta.CANCELADA);
    }

    public void marcarNaoCompareceu() {
        mudarStatus(StatusConsulta.NAO_COMPARECEU);
    }

    public boolean conflitaCom(Consulta outra) {
        return outra != this
                && status != StatusConsulta.CANCELADA && status != StatusConsulta.NAO_COMPARECEU
                && outra.status != StatusConsulta.CANCELADA && outra.status != StatusConsulta.NAO_COMPARECEU
                && getProfissional().getId().equals(outra.getProfissional().getId())
                && getData().equals(outra.getData())
                && getHorario().equals(outra.getHorario());
    }

    private void mudarStatus(StatusConsulta novo) {
        if (!status.podeIrPara(novo)) {
            throw new IllegalStateException("A consulta não pode passar de " + status + " para " + novo + ".");
        }
        status = novo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public StatusConsulta getStatus() {
        return status;
    }
}
