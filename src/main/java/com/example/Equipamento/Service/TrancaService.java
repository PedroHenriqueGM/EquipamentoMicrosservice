package com.example.Equipamento.Service;


import com.example.Equipamento.Model.Tranca;
import com.example.Equipamento.Repository.TrancaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TrancaService {
    private final TrancaRepository repository;
    public TrancaService(TrancaRepository repository) {
        this.repository = repository;
    }

    public void salvarTranca(Tranca tranca) {
        repository.saveAndFlush(tranca);
    }


    public void deletarTrancaPorNumero(String numero) {
        repository.deleteByNumero(numero);
    }

    public void atualizarTrancaPorId(Integer id, Tranca bicicleta) {
        Tranca trancaEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tranca não encontrada"));

        // R3: número NÃO pode ser alterado -> preserva sempre o número atual
        String numeroOriginal = trancaEntity.getNumero();

        if (bicicleta.getNumero() != null
                && !bicicleta.getNumero().equals(trancaEntity.getNumero())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "R3: o número da bicicleta não pode ser alterado."
            );
        }

        Tranca trancaAtualizada = Tranca.builder()
                .id(trancaEntity.getId())
                .numero(numeroOriginal) // <- força manter o número original
                .status(tranca.getStatus() != null ? tranca.getStatus() : trancaEntity.getStatus())
                .modelo(tranca.getModelo() != null ? tranca.getModelo() : trancaEntity.getModelo())
                .ano(tranca.getAno() != null ? tranca.getAno() : trancaEntity.getAno())
                .localizacao(tranca.getLocalizacao() != null ? tranca.getLocalizacao() : trancaEntity.getLocalizacao())
                .build();

        repository.saveAndFlush(trancaAtualizada);
    }




}