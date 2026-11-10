package br.pucminas.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Paciente paciente;

    @ManyToOne
    private Profissional profissional;

    private LocalDate data;

    private LocalTime horario;

    @Column(length = 2000)
    private String observacoes;

    public boolean validarCondicoes() {
        if (paciente == null) {
            throw new IllegalStateException("Informe o paciente.");
        }
        if (profissional == null) {
            throw new IllegalStateException("Informe o profissional responsável.");
        }
        if (data == null || horario == null) {
            throw new IllegalStateException("Informe a data e o horário.");
        }
        return true;
    }

    public abstract String getTipo();

    public abstract String gerarResumo();

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
