package br.pucminas.hospital.service;

import br.pucminas.hospital.model.Internacao;
import br.pucminas.hospital.model.Quarto;
import br.pucminas.hospital.model.SituacaoQuarto;
import br.pucminas.hospital.repository.AtendimentoRepository;
import br.pucminas.hospital.repository.QuartoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final AtendimentoRepository atendimentoRepository;

    public QuartoService(QuartoRepository quartoRepository, AtendimentoRepository atendimentoRepository) {
        this.quartoRepository = quartoRepository;
        this.atendimentoRepository = atendimentoRepository;
    }

    public List<Quarto> listar(SituacaoQuarto situacao) {
        return quartoRepository.findAll().stream()
                .filter(q -> situacao == null || q.getSituacao() == situacao)
                .toList();
    }

    public Quarto buscar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Informe o quarto.");
        }
        return quartoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quarto não encontrado."));
    }

    @Transactional
    public Quarto criar(Quarto quarto) {
        validar(quarto);
        if (quartoRepository.existsByNumIdentificacao(quarto.getNumIdentificacao())) {
            throw new IllegalArgumentException("Já existe um quarto com este número.");
        }
        return quartoRepository.save(quarto);
    }

    @Transactional
    public Quarto atualizar(Long id, Quarto dados) {
        Quarto quarto = buscar(id);
        if (!quarto.getNumIdentificacao().equals(dados.getNumIdentificacao())
                && quartoRepository.existsByNumIdentificacao(dados.getNumIdentificacao())) {
            throw new IllegalArgumentException("Já existe um quarto com este número.");
        }
        quarto.setNumIdentificacao(dados.getNumIdentificacao());
        quarto.setAndar(dados.getAndar());
        quarto.setCapMax(dados.getCapMax());
        quarto.setTipo(dados.getTipo());
        validar(quarto);
        return quarto;
    }

    @Transactional
    public void remover(Long id) {
        Quarto quarto = buscar(id);
        boolean usado = atendimentoRepository.listar(Internacao.class).stream()
                .anyMatch(i -> i.getQuarto().getId().equals(id));
        if (usado) {
            throw new IllegalStateException("O quarto possui internações registradas e não pode ser excluído.");
        }
        quartoRepository.delete(quarto);
    }

    private void validar(Quarto quarto) {
        if (quarto.getNumIdentificacao() == null || quarto.getAndar() == null
                || quarto.getCapMax() == null || quarto.getTipo() == null) {
            throw new IllegalArgumentException("Informe número, andar, capacidade máxima e tipo do quarto.");
        }
        quarto.setSituacao(quarto.possuiVaga() ? SituacaoQuarto.DISPONIVEL : SituacaoQuarto.OCUPADO);
    }
}
