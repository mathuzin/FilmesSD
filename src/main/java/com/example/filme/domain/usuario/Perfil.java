package com.example.filme.domain.usuario;


import lombok.Getter;

@Getter
public enum Perfil {
    COMUM("comum"),
    ADM("adm");

    private String perfil;

    Perfil(String perfil) {
        this.perfil = perfil;
    }
}
