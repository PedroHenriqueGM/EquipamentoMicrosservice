package com.example.Equipamento.Service;

import com.example.Equipamento.Model.Bicicleta;
import com.example.Equipamento.Repository.BicicletaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BicicletaService {
    private final BicicletaRepository repository;
    public BicicletaService(BicicletaRepository repository) {
        this.repository = repository;
    }

    public void salvarBicicleta(Bicicleta bicicleta) {
        repository.saveAndFlush(bicicleta);
    }

    public void deletarBicicletaPorNumero(String numero) {
        repository.deleteByNumero(numero);
    }

    public void atualizarBicicletaPorId(Integer id, Bicicleta bicicleta) {
        Bicicleta bicicletaEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bicicleta não encontrada"));

        // R3: número NÃO pode ser alterado -> preserva sempre o número atual
        String numeroOriginal = bicicletaEntity.getNumero();

        if (bicicleta.getNumero() != null
                && !bicicleta.getNumero().equals(bicicletaEntity.getNumero())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "R3: o número da bicicleta não pode ser alterado."
            );
        }

        Bicicleta bicicletaAtualizada = Bicicleta.builder()
                .id(bicicletaEntity.getId())
                .numero(numeroOriginal) // <- força manter o número original
                .status(bicicleta.getStatus() != null ? bicicleta.getStatus() : bicicletaEntity.getStatus())
                .modelo(bicicleta.getModelo() != null ? bicicleta.getModelo() : bicicletaEntity.getModelo())
                .marca(bicicleta.getMarca() != null ? bicicleta.getMarca() : bicicletaEntity.getMarca())
                .ano(bicicleta.getAno() != null ? bicicleta.getAno() : bicicletaEntity.getAno())
                .localizacao(bicicleta.getLocalizacao() != null ? bicicleta.getLocalizacao() : bicicletaEntity.getLocalizacao())
                .build();

        repository.saveAndFlush(bicicletaAtualizada);
    }




}
