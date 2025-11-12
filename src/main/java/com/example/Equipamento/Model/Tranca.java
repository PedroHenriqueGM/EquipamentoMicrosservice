package com.example.Equipamento.Model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tranca")
@Entity
public class Tranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "numero", unique = true)
    private String numero;

    @Column(name = "status")
    private String status;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "localização")
    private String localizacao;

    @Column(name = "ano")
    private String ano;
}
