package com.example.Equipamento.Controller;


import com.example.Equipamento.Model.Tranca;
import com.example.Equipamento.Repository.TrancaRepository;
import com.example.Equipamento.Service.TrancaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tranca")
@RequiredArgsConstructor
public class TrancaController {
    private final TrancaService trancaService;

    @PostMapping
    public ResponseEntity<Void> salvarTranca(@RequestBody Tranca tranca) {
        trancaService.salvarTranca(tranca);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private TrancaRepository trancaRepository;

    @GetMapping
    public ResponseEntity<List<Tranca>> listarTrancas() {
        List<Tranca> trancas = trancaRepository.findAll();
        return ResponseEntity.ok(trancas);
    }


    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> deletarTrancaPorNumero(@PathVariable String numero) {
        trancaService.deletarTrancaPorNumero(numero);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarTrancaPorId(
            @PathVariable Integer id,
            @RequestBody Tranca tranca) {
        trancaService.atualizarTrancaPorId(id, tranca);
        return ResponseEntity.ok().build();
    }

    // TrancaController.java (acrescente)
    @PutMapping("/{numero}/trancar")
    public ResponseEntity<Void> trancar(
            @PathVariable String numero,
            @RequestParam(value = "bicicletaNumero", required = false) String bicicletaNumero) {
        trancaService.trancarPorNumero(numero, bicicletaNumero);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{numero}/destrancar")
    public ResponseEntity<Void> destrancar(@PathVariable String numero) {
        trancaService.destrancarPorNumero(numero);
        return ResponseEntity.ok().build();
    }


}
