package br.pucminas.hospital.model;

import jakarta.persistence.Entity;

@Entity
public class ConsultaRotina extends Consulta {

    @Override
    public String getTipo() {
        return "Consulta de rotina";
    }

    @Override
    public String gerarResumo() {
        return getTipo() + " em " + getData() + " às " + getHorario() + " com "
                + getProfissional().getNome() + ": " + getMotivo() + " (" + getStatus() + ")";
    }
}
