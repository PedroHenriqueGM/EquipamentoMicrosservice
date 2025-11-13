package com.example.Equipamento.Service;

import com.example.Equipamento.Model.Bicicleta;
import com.example.Equipamento.Model.Tranca;
import com.example.Equipamento.Repository.BicicletaRepository;
import com.example.Equipamento.Repository.TrancaRepository;
import com.example.Equipamento.Repository.TotemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TrancaService {
    private final TrancaRepository repository;
    private final BicicletaRepository bicicletaRepository;
    private final TotemRepository totemRepository;

    public TrancaService(TrancaRepository repository, BicicletaRepository bicicletaRepository,TotemRepository totemRepository) {
        this.repository = repository;
        this.bicicletaRepository = bicicletaRepository;
        this.totemRepository = totemRepository; 
    }

    private static final String MSG_TRANCA_NAO_ENCONTRADA = "Tranca não encontrada";

    public void salvarTranca(Tranca tranca) {
        repository.saveAndFlush(tranca);
    }

    public void deletarTranca(Integer id) {
        repository.deleteById(id);
    }

    public void atualizarTrancaPorId(Integer id, Tranca req) {
        Tranca entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_TRANCA_NAO_ENCONTRADA));

        if (req.getNumero() != null && !req.getNumero().equals(entity.getNumero())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "R3: o número da tranca não pode ser alterado.");
        }

        if (req.getStatus() != null) entity.setStatus(req.getStatus());
        if (req.getModelo() != null) entity.setModelo(req.getModelo());
        if (req.getAno() != null) entity.setAno(req.getAno());
        if (req.getLocalizacao() != null) entity.setLocalizacao(req.getLocalizacao());

        repository.saveAndFlush(entity);
    }

    public void trancar(Integer idTranca, String idBicicleta) {
        Tranca tranca = repository.findById(idTranca)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_TRANCA_NAO_ENCONTRADA));

        if ("ocupada".equalsIgnoreCase(tranca.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tranca já está ocupada");
        }

        Bicicleta bike = null;
        if (idBicicleta != null) {
            bike = bicicletaRepository.findByNumero(idBicicleta)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bicicleta não encontrada"));

            // opcional: se já estiver em outra tranca, barre
            if (repository.existsByBicicletaId(bike.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta bicicleta já está presa em outra tranca");
            }

            // status da bicicleta ao trancar
            bike.setStatus("travada");
            bicicletaRepository.saveAndFlush(bike);

            // faz o vínculo
            tranca.setBicicleta(bike);
        }

        // status da tranca ao trancar
        tranca.setStatus("ocupada");
        repository.saveAndFlush(tranca);
    }

    public void destrancar(Integer idTranca) {
        Tranca tranca = repository.findById(idTranca)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MSG_TRANCA_NAO_ENCONTRADA));

        if ("livre".equalsIgnoreCase(tranca.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tranca já está livre");
        }

        // se houver bicicleta, atualiza status e remove vínculo
        Bicicleta bike = tranca.getBicicleta();
        if (bike != null) {
            bike.setStatus("disponivel"); // ou “nova”/“em_uso” conforme seu fluxo
            bicicletaRepository.saveAndFlush(bike);
            tranca.setBicicleta(null);
        }

        tranca.setStatus("livre");
        repository.saveAndFlush(tranca);
    }

    public List<Tranca> listarTrancasDoTotem(Long idTotem) {
        if (!totemRepository.existsById(idTotem)) {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Totem não encontrado.");
        }
        
        return repository.findByTotemId(idTotem);
    }

}
