package com.example.Equipamento.Service;

import com.example.Equipamento.Model.Bicicleta;
import com.example.Equipamento.Model.Tranca;
import com.example.Equipamento.Repository.BicicletaRepository;
import com.example.Equipamento.Repository.TrancaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrancaServiceTest {

    @Mock TrancaRepository trancaRepository;
    @Mock BicicletaRepository bicicletaRepository;

    @InjectMocks TrancaService service;

    @Test
    void trancarPorNumero_deveVincularEBloquearStatus() {
        Tranca tranca = Tranca.builder().id(3).numero("TR-01").status("livre").build();
        Bicicleta bike = Bicicleta.builder().id(9).numero("BIC-9").status("disponivel").build();

        when(trancaRepository.findByNumero("TR-01")).thenReturn(Optional.of(tranca));
        when(bicicletaRepository.findByNumero("BIC-9")).thenReturn(Optional.of(bike));
        when(trancaRepository.existsByBicicletaId(9)).thenReturn(false);

        service.trancarPorNumero("TR-01", "BIC-9");

        assertThat(tranca.getStatus()).isEqualToIgnoringCase("ocupada");
        assertThat(tranca.getBicicleta()).isEqualTo(bike);
        assertThat(bike.getStatus()).isEqualToIgnoringCase("travada");

        verify(trancaRepository).saveAndFlush(tranca);
        verify(bicicletaRepository).saveAndFlush(bike);
    }

    @Test
    void destrancarPorNumero_deveDesvincularELiberarStatus() {
        Bicicleta bike = Bicicleta.builder().id(9).numero("BIC-9").status("travada").build();
        Tranca tranca = Tranca.builder().id(3).numero("TR-01").status("ocupada").bicicleta(bike).build();

        when(trancaRepository.findByNumero("TR-01")).thenReturn(Optional.of(tranca));

        service.destrancarPorNumero("TR-01");

        assertThat(tranca.getStatus()).isEqualToIgnoringCase("livre");
        assertThat(tranca.getBicicleta()).isNull();
        assertThat(bike.getStatus()).isEqualToIgnoringCase("disponivel");

        verify(trancaRepository).saveAndFlush(tranca);
        verify(bicicletaRepository).saveAndFlush(bike);
    }

    @Test
    void trancarPorNumero_deveFalharSeTrancaJaOcupada_ouBikeEmOutraTranca() {
        Tranca tranca = Tranca.builder().id(1).numero("TR-02").status("ocupada").build();
        when(trancaRepository.findByNumero("TR-02")).thenReturn(Optional.of(tranca));

        assertThatThrownBy(() -> service.trancarPorNumero("TR-02", "BIC-1"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void salvarTranca_deveChamarSaveAndFlush() {
        Tranca tranca = Tranca.builder()
                .numero("TR-01")
                .status("livre")
                .modelo("SmartLock")
                .ano("2024")
                .localizacao("Estação A")
                .build();

        service.salvarTranca(tranca);

        verify(trancaRepository, times(1)).saveAndFlush(tranca);
    }

    @Test
    void atualizarTrancaPorId_sucessoEAoMudarNumeroDeveFalhar() {
        Tranca existente = Tranca.builder()
                .id(10)
                .numero("TR-10")
                .status("livre")
                .modelo("M1")
                .ano("2023")
                .localizacao("Ponto 1")
                .build();

        when(trancaRepository.findById(10)).thenReturn(Optional.of(existente));

        // sucesso: altera campos permitidos
        Tranca reqOk = Tranca.builder()
                .modelo("M2")
                .ano("2025")
                .localizacao("Ponto 2")
                .build();

        service.atualizarTrancaPorId(10, reqOk);

        assertThat(existente.getModelo()).isEqualTo("M2");
        assertThat(existente.getAno()).isEqualTo("2025");
        assertThat(existente.getLocalizacao()).isEqualTo("Ponto 2");
        assertThat(existente.getNumero()).isEqualTo("TR-10"); // número preservado

        verify(trancaRepository, times(1)).saveAndFlush(existente);

        // erro: tentativa de mudar o número
        Tranca reqErro = Tranca.builder().numero("TR-99").build();
        assertThatThrownBy(() -> service.atualizarTrancaPorId(10, reqErro))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("número da tranca não pode ser alterado");
    }

    @Test
    void deletarTrancaPorNumero_deveChamarDeleteByNumero() {
        service.deletarTrancaPorNumero("TR-07");
        verify(trancaRepository, times(1)).deleteByNumero("TR-07");
    }
}
