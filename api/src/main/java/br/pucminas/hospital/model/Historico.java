package br.pucminas.hospital.model;

import java.util.ArrayList;
import java.util.List;

public class Historico {

    private final List<Atendimento> atendimentos = new ArrayList<>();
    private final Paciente paciente;

    public Historico(Paciente paciente) {
        this.paciente = paciente;
    }

    public void adicionar(Atendimento atendimento) {
        atendimentos.add(atendimento);
    }

    public List<Consulta> getConsultas() {
        return atendimentos.stream().filter(Consulta.class::isInstance).map(Consulta.class::cast).toList();
    }

    public List<Internacao> getInternacoes() {
        return atendimentos.stream().filter(Internacao.class::isInstance).map(Internacao.class::cast).toList();
    }

    public Paciente getPaciente() {
        return paciente;
    }
}
