package br.pucminas.hospital.repository;

import br.pucminas.hospital.model.Quarto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {

    boolean existsByNumIdentificacao(Integer numIdentificacao);
}
