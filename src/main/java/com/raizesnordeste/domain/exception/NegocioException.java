package com.raizesnordeste.domain.exception;

import lombok.Getter;

@Getter
public class NegocioException extends RuntimeException {
    private final String codigoErro;

    public NegocioException(String codigoErro, String mensagem) {
        super(mensagem);
        this.codigoErro = codigoErro;
    }
}
