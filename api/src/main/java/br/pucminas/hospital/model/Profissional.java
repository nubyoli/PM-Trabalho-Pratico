package br.pucminas.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "categoria")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClinicoGeral.class, name = "CLINICO_GERAL"),
        @JsonSubTypes.Type(value = Especialista.class, name = "ESPECIALISTA"),
        @JsonSubTypes.Type(value = Cirurgiao.class, name = "CIRURGIAO")
})
public abstract class Profissional extends Pessoa {

    @NotBlank(message = "Informe o registro profissional")
    @Column(unique = true)
    private String regProf;

    @JsonIgnore
    @OneToMany(mappedBy = "profissional")
    private List<Atendimento> atendimentos = new ArrayList<>();

    public boolean estaDisponivel(LocalDate data, LocalTime horario) {
        return atendimentos.stream()
                .filter(a -> data.equals(a.getData()) && horario.equals(a.getHorario()))
                .noneMatch(a -> (a instanceof Consulta c
                        && c.getStatus() != StatusConsulta.CANCELADA
                        && c.getStatus() != StatusConsulta.NAO_COMPARECEU)
                        || (a instanceof ProcedimentoCirurgico p && p.getStatus() != StatusProcedimento.CANCELADO));
    }

    public String getDescricao() {
        return getNome() + " (" + regProf + ")";
    }

    public String getRegProf() {
        return regProf;
    }

    public void setRegProf(String regProf) {
        this.regProf = regProf;
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }
}
