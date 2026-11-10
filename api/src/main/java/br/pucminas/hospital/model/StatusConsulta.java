package br.pucminas.hospital.model;

public enum StatusConsulta {
    AGENDADA, CONFIRMADA, EM_ATENDIMENTO, CONCLUIDA, CANCELADA, NAO_COMPARECEU;

    public boolean podeIrPara(StatusConsulta novo) {
        return switch (this) {
            case AGENDADA -> novo == CONFIRMADA || novo == CANCELADA || novo == NAO_COMPARECEU;
            case CONFIRMADA -> novo == EM_ATENDIMENTO || novo == CANCELADA || novo == NAO_COMPARECEU;
            case EM_ATENDIMENTO -> novo == CONCLUIDA;
            case CONCLUIDA, CANCELADA, NAO_COMPARECEU -> false;
        };
    }
}
