package br.pucminas.hospital.model;

import jakarta.persistence.Entity;

@Entity
public class Paciente extends Pessoa {

    private String endereco;

    public String getDescricao() {
        return getNome() + " (CPF " + getCpf() + ")";
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
