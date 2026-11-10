package br.pucminas.hospital.repository;

import br.pucminas.hospital.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    boolean existsByRegProf(String regProf);
}
