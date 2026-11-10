package br.pucminas.hospital.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Internacao extends Atendimento {

    @ManyToOne
    @JsonIgnoreProperties("pacientesHospitalizados")
    private Quarto quarto;

    private LocalDate dataEntrada;

    private LocalDate dataPrevistaAlta;

    private LocalDate dataEfetAlta;

    @Override
    public boolean validarCondicoes() {
        super.validarCondicoes();
        if (quarto == null) {
            throw new IllegalStateException("Informe o quarto.");
        }
        if (dataEntrada == null) {
            throw new IllegalStateException("Informe a data de entrada.");
        }
        if (dataPrevistaAlta == null || dataPrevistaAlta.isBefore(dataEntrada)) {
            throw new IllegalStateException("Informe uma data prevista de alta igual ou posterior à data de entrada.");
        }
        return true;
    }

    public void registrarEntrada() {
        validarCondicoes();
        quarto.ocupar(getPaciente());
    }

    public void registrarAlta(LocalDate dataAlta) {
        if (!estaAtiva()) {
            throw new IllegalStateException("A alta desta internação já foi registrada.");
        }
        if (dataAlta == null || dataAlta.isBefore(dataEntrada)) {
            throw new IllegalArgumentException("Informe uma data de alta igual ou posterior à data de entrada.");
        }
        dataEfetAlta = dataAlta;
        quarto.liberar(getPaciente());
    }

    public boolean estaAtiva() {
        return dataEfetAlta == null;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
        setData(dataEntrada);
    }

    @Override
    public String getTipo() {
        return "Internação";
    }

    @Override
    public String gerarResumo() {
        String resumo = getTipo() + " no quarto " + quarto.getNumIdentificacao() + " desde " + dataEntrada
                + " com " + getProfissional().getNome();
        return estaAtiva() ? resumo + " (ativa)" : resumo + ", alta em " + dataEfetAlta;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public LocalDate getDataPrevistaAlta() {
        return dataPrevistaAlta;
    }

    public void setDataPrevistaAlta(LocalDate dataPrevistaAlta) {
        this.dataPrevistaAlta = dataPrevistaAlta;
    }

    public LocalDate getDataEfetAlta() {
        return dataEfetAlta;
    }
}
