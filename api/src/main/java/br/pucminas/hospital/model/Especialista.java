package br.pucminas.hospital.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Especialista extends Profissional {

    @NotBlank(message = "Informe a especialidade")
    private String especialidade;

    public void realizarConsulta(ConsultaEspecializada consulta) {
        consulta.iniciar();
    }

    public void registrarObservacoes(ConsultaEspecializada consulta, String observacoes) {
        consulta.setObservacoes(observacoes);
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
