package br.pucminas.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class Cirurgiao extends Profissional {

    @NotBlank(message = "Informe a área cirúrgica")
    private String areaCirurgica;

    public void realizarProcedimento(ProcedimentoCirurgico procedimento) {
        procedimento.iniciar();
    }

    public void registrarProcedimento(ProcedimentoCirurgico procedimento) {
        procedimento.setProfissional(this);
        procedimento.validarCondicoes();
        if (!estaDisponivel(procedimento.getData(), procedimento.getHorario())) {
            throw new IllegalStateException(getNome() + " já possui um atendimento nesse horário.");
        }
        getAtendimentos().add(procedimento);
    }

    @JsonIgnore
    public List<ProcedimentoCirurgico> getProcedimentos() {
        return getAtendimentos().stream()
                .filter(ProcedimentoCirurgico.class::isInstance)
                .map(ProcedimentoCirurgico.class::cast)
                .toList();
    }

    public String getAreaCirurgica() {
        return areaCirurgica;
    }

    public void setAreaCirurgica(String areaCirurgica) {
        this.areaCirurgica = areaCirurgica;
    }
}
