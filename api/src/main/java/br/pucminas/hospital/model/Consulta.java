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
        status = StatusConsulta.CONFIRMADA;
    }

    public void iniciar() {
        status = StatusConsulta.EM_ATENDIMENTO;
    }

    public void concluir(String observacoes) {
        status = StatusConsulta.CONCLUIDA;
        if (observacoes != null && !observacoes.isBlank()) {
            setObservacoes(observacoes);
        }
    }

    public void cancelar() {
        status = StatusConsulta.CANCELADA;
    }

    public void marcarNaoCompareceu() {
        status = StatusConsulta.NAO_COMPARECEU;
    }

    public boolean conflitaCom(Consulta outra) {
        return outra != this
                && getProfissional().getId().equals(outra.getProfissional().getId())
                && getData().equals(outra.getData())
                && getHorario().equals(outra.getHorario());
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
