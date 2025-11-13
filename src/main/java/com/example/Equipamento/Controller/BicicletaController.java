package com.example.Equipamento.Controller;

import com.example.Equipamento.Model.Bicicleta;
import com.example.Equipamento.Repository.BicicletaRepository;
import com.example.Equipamento.Service.BicicletaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bicicleta")
@RequiredArgsConstructor
public class BicicletaController {
    private final BicicletaService bicicletaService;

    @PostMapping
    public ResponseEntity<Void> salvarBicicleta(@RequestBody Bicicleta bicicleta) {
        bicicletaService.salvarBicicleta(bicicleta);
        return ResponseEntity.ok().build();
    }

    @Autowired
    private BicicletaRepository bicicletaRepository;

    @GetMapping
    public ResponseEntity<List<Bicicleta>> listarBicicletas() {
        List<Bicicleta> bicicletas = bicicletaRepository.findAll();
        return ResponseEntity.ok(bicicletas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bicicleta> buscarPorId(@PathVariable Integer id){
        return ResponseEntity.ok(bicicletaService.buscarPorId(id));
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> deletarBicicletaPorNumero(@PathVariable String numero) {
        bicicletaService.deletarBicicletaPorNumero(numero);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarBicicletaPorId(
            @PathVariable Integer id,
            @RequestBody Bicicleta bicicleta) {
        bicicletaService.atualizarBicicletaPorId(id, bicicleta);
        return ResponseEntity.ok().build();
    }

}
