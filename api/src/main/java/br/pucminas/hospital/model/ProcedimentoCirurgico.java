package br.pucminas.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

@Entity
public class ProcedimentoCirurgico extends Atendimento {

    private String descricao;

    private String tipoCirurgia;

    @Column(length = 2000)
    private String obsPreOperatorias;

    @Column(length = 2000)
    private String obsPosOperatorias;

    private LocalDateTime dataHoraPrevista;

    private LocalDateTime dataHoraEfetiva;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_procedimento")
    private StatusProcedimento status = StatusProcedimento.AGENDADO;

    @Override
    public boolean validarCondicoes() {
        super.validarCondicoes();
        if (!(getProfissional() instanceof Cirurgiao)) {
            throw new IllegalStateException("O procedimento deve ser realizado por um cirurgião.");
        }
        if (descricao == null || descricao.isBlank() || tipoCirurgia == null || tipoCirurgia.isBlank()) {
            throw new IllegalStateException("Informe a descrição e o tipo de cirurgia.");
        }
        return true;
    }

    public void iniciar() {
        if (status != StatusProcedimento.AGENDADO) {
            throw new IllegalStateException("Só é possível iniciar um procedimento agendado.");
        }
        status = StatusProcedimento.EM_ANDAMENTO;
        dataHoraEfetiva = LocalDateTime.now();
    }

    public void concluir() {
        if (status != StatusProcedimento.EM_ANDAMENTO) {
            throw new IllegalStateException("Não é possível concluir um procedimento que não foi iniciado.");
        }
        status = StatusProcedimento.CONCLUIDO;
    }

    public void cancelar() {
        if (status != StatusProcedimento.AGENDADO) {
            throw new IllegalStateException("Só é possível cancelar um procedimento agendado.");
        }
        status = StatusProcedimento.CANCELADO;
    }

    public LocalDateTime getDataHoraPrevista() {
        return dataHoraPrevista;
    }

    public void setDataHoraPrevista(LocalDateTime dataHoraPrevista) {
        this.dataHoraPrevista = dataHoraPrevista;
        if (dataHoraPrevista != null) {
            setData(dataHoraPrevista.toLocalDate());
            setHorario(dataHoraPrevista.toLocalTime());
        }
    }

    @Override
    public String getTipo() {
        return "Procedimento cirúrgico";
    }

    @Override
    public String gerarResumo() {
        return getTipo() + " (" + tipoCirurgia + "): " + descricao + ", previsto para " + dataHoraPrevista
                + " com " + getProfissional().getNome() + " (" + status + ")";
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoCirurgia() {
        return tipoCirurgia;
    }

    public void setTipoCirurgia(String tipoCirurgia) {
        this.tipoCirurgia = tipoCirurgia;
    }

    public String getObsPreOperatorias() {
        return obsPreOperatorias;
    }

    public void setObsPreOperatorias(String obsPreOperatorias) {
        this.obsPreOperatorias = obsPreOperatorias;
    }

    public String getObsPosOperatorias() {
        return obsPosOperatorias;
    }

    public void setObsPosOperatorias(String obsPosOperatorias) {
        this.obsPosOperatorias = obsPosOperatorias;
    }

    public LocalDateTime getDataHoraEfetiva() {
        return dataHoraEfetiva;
    }

    public void setDataHoraEfetiva(LocalDateTime dataHoraEfetiva) {
        this.dataHoraEfetiva = dataHoraEfetiva;
    }

    public StatusProcedimento getStatus() {
        return status;
    }
}
