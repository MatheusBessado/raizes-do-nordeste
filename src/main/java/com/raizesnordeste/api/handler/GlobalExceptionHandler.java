package com.raizesnordeste.api.handler;

import com.raizesnordeste.domain.exception.NegocioException;
import com.raizesnordeste.domain.exception.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> handleNegocio(NegocioException ex,
                                                              HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(erroBody(ex.getCodigoErro(), ex.getMessage(), List.of(), req.getRequestURI()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(RecursoNaoEncontradoException ex,
                                                               HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(erroBody("RECURSO_NAO_ENCONTRADO", ex.getMessage(), List.of(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                  HttpServletRequest req) {
        List<Map<String, String>> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of("field", e.getField(), "issue", e.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(erroBody("VALIDACAO_FALHOU", "Dados de entrada inválidos.", detalhes, req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(erroBody("ACESSO_NEGADO", "Você não tem permissão para esta operação.", List.of(), req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(erroBody("ERRO_INTERNO", "Erro inesperado. Tente novamente.", List.of(), req.getRequestURI()));
    }

    private Map<String, Object> erroBody(String error, String message, Object details, String path) {
        return Map.of(
            "error", error,
            "message", message,
            "details", details,
            "timestamp", Instant.now().toString(),
            "path", path,
            "requestId", UUID.randomUUID().toString()
        );
    }
}
