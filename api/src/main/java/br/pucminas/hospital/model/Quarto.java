package br.pucminas.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer numIdentificacao;

    private Integer andar;

    private Integer capMax;

    @Enumerated(EnumType.STRING)
    private SituacaoQuarto situacao = SituacaoQuarto.DISPONIVEL;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Paciente> pacientesHospitalizados = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TipoQuarto tipo;

    public boolean possuiVaga() {
        return pacientesHospitalizados.size() < capMax;
    }

    public void ocupar(Paciente paciente) {
        if (pacientesHospitalizados.stream().anyMatch(p -> p.getId().equals(paciente.getId()))) {
            throw new IllegalStateException(paciente.getNome() + " já está no quarto " + numIdentificacao + ".");
        }
        if (!possuiVaga()) {
            throw new IllegalStateException("O quarto " + numIdentificacao + " atingiu a capacidade máxima.");
        }
        pacientesHospitalizados.add(paciente);
        situacao = possuiVaga() ? SituacaoQuarto.DISPONIVEL : SituacaoQuarto.OCUPADO;
    }

    public void liberar(Paciente paciente) {
        if (!pacientesHospitalizados.removeIf(p -> p.getId().equals(paciente.getId()))) {
            throw new IllegalStateException(paciente.getNome() + " não está no quarto " + numIdentificacao + ".");
        }
        situacao = possuiVaga() ? SituacaoQuarto.DISPONIVEL : SituacaoQuarto.OCUPADO;
    }

    public Long getId() {
        return id;
    }

    public Integer getNumIdentificacao() {
        return numIdentificacao;
    }

    public void setNumIdentificacao(Integer numIdentificacao) {
        this.numIdentificacao = numIdentificacao;
    }

    public Integer getAndar() {
        return andar;
    }

    public void setAndar(Integer andar) {
        this.andar = andar;
    }

    public Integer getCapMax() {
        return capMax;
    }

    public void setCapMax(Integer capMax) {
        this.capMax = capMax;
    }

    public SituacaoQuarto getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoQuarto situacao) {
        this.situacao = situacao;
    }

    public List<Paciente> getPacientesHospitalizados() {
        return pacientesHospitalizados;
    }

    public TipoQuarto getTipo() {
        return tipo;
    }

    public void setTipo(TipoQuarto tipo) {
        this.tipo = tipo;
    }
}
