package com.example.Equipamento.Repository;


import com.example.Equipamento.Model.Tranca;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrancaRepository extends JpaRepository<Tranca, Integer> {

    Optional<Tranca> findByNumero(String numero);

    @Transactional
    void deleteByNumero(String numero);
}