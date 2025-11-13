package com.example.Equipamento.Service;

import com.example.Equipamento.Model.Bicicleta;
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
class BicicletaServiceTest {

    @Mock BicicletaRepository bicicletaRepository;
    @Mock TrancaRepository trancaRepository;

    @InjectMocks BicicletaService service;

    @Test
    void salvarBicicleta_deveDefinirStatusNova_eGerarNumeroComId() {
        // Arrange
        Bicicleta input = Bicicleta.builder()
                .marca("Caloi").modelo("Elite").ano("2024").localizacao("Depósito").build();

        Bicicleta persisted = Bicicleta.builder()
                .id(10).marca("Caloi").modelo("Elite").ano("2024").localizacao("Depósito").build();

        when(bicicletaRepository.saveAndFlush(any(Bicicleta.class)))
                .thenReturn(persisted) // 1ª chamada (gera id)
                .thenAnswer(invocation -> invocation.getArgument(0)); // 2ª chamada (com numero setado)

        // Act
        service.salvarBicicleta(input);

        // Assert
        // 1ª chamada: status = "nova"
        ArgumentCaptor<Bicicleta> captor = ArgumentCaptor.forClass(Bicicleta.class);
        verify(bicicletaRepository, times(2)).saveAndFlush(captor.capture());

        Bicicleta primeira = captor.getAllValues().get(0);
        Bicicleta segunda  = captor.getAllValues().get(1);

        assertThat(primeira.getStatus()).isEqualTo("nova");
        assertThat(segunda.getNumero()).isEqualTo("BIC-10"); // R5
    }

    @Test
    void atualizarBicicletaPorId_naoPermiteAlterarNumero_ouStatus() {
        // Arrange
        Bicicleta existente = Bicicleta.builder()
                .id(5).numero("BIC-5").status("nova")
                .marca("A").modelo("B").ano("2023").localizacao("X")
                .build();

        when(bicicletaRepository.findById(5)).thenReturn(Optional.of(existente));

        // Tentativa de alterar número e status → deve dar 400
        Bicicleta req = Bicicleta.builder()
                .numero("BIC-999").status("qualquer")
                .marca("Nova").build();

        // Act + Assert
        assertThatThrownBy(() -> service.atualizarBicicletaPorId(5, req))
                .isInstanceOf(ResponseStatusException.class);

        // Agora testando apenas troca de campos permitidos
        Bicicleta reqOk = Bicicleta.builder().marca("Sense").build();
        service.atualizarBicicletaPorId(5, reqOk);

        verify(bicicletaRepository, times(1)).saveAndFlush(existente);
        assertThat(existente.getMarca()).isEqualTo("Sense");
        assertThat(existente.getNumero()).isEqualTo("BIC-5");
        assertThat(existente.getStatus()).isEqualTo("nova");
    }

    @Test
    void deletarBicicletaPorNumero_apenasAposentadaESemTranca() {
        // Arrange
        Bicicleta b = Bicicleta.builder()
                .id(7).numero("BIC-7").status("aposentada")
                .build();

        when(bicicletaRepository.findByNumero("BIC-7")).thenReturn(Optional.of(b));
        when(trancaRepository.existsByBicicletaId(7)).thenReturn(false);

        // Act
        service.deletarBicicletaPorNumero("BIC-7");

        // Assert
        assertThat(b.getStatus()).isEqualTo("excluida");
        verify(bicicletaRepository).saveAndFlush(b);
    }

    @Test
    void deletarBicicletaPorNumero_deveFalharSeNaoAposentada_ouEmTranca() {
        Bicicleta b1 = Bicicleta.builder().id(1).numero("BIC-1").status("nova").build();
        when(bicicletaRepository.findByNumero("BIC-1")).thenReturn(Optional.of(b1));

        assertThatThrownBy(() -> service.deletarBicicletaPorNumero("BIC-1"))
                .isInstanceOf(ResponseStatusException.class);

        Bicicleta b2 = Bicicleta.builder().id(2).numero("BIC-2").status("aposentada").build();
        when(bicicletaRepository.findByNumero("BIC-2")).thenReturn(Optional.of(b2));
        when(trancaRepository.existsByBicicletaId(2)).thenReturn(true);

        assertThatThrownBy(() -> service.deletarBicicletaPorNumero("BIC-2"))
                .isInstanceOf(ResponseStatusException.class);
    }

}
