package br.pucminas.hospital.model;

import jakarta.persistence.Entity;

@Entity
public class ConsultaEspecializada extends Consulta {

    private String especialidade;

    @Override
    public boolean validarCondicoes() {
        super.validarCondicoes();
        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalStateException("Informe a especialidade da consulta.");
        }
        return true;
    }

    @Override
    public String getTipo() {
        return "Consulta especializada";
    }

    @Override
    public String gerarResumo() {
        return "Consulta de " + especialidade + " em " + getData() + " às " + getHorario() + " com "
                + getProfissional().getNome() + ": " + getMotivo() + " (" + getStatus() + ")";
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
