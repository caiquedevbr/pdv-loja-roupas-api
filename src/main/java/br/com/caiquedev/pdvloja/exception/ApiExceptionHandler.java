package br.com.caiquedev.pdvloja.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
    }

    @ExceptionHandler({BusinessConflictException.class, DataIntegrityViolationException.class})
    public ProblemDetail handleConflict(RuntimeException ex) {
        String detail = ex instanceof DataIntegrityViolationException
                ? "A operação viola uma restrição de integridade ou conflita com dados existentes."
                : ex.getMessage();
        return problem(HttpStatus.CONFLICT, "Conflito de dados", detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> invalidFields = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            invalidFields.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "Corrija os campos informados e envie a requisição novamente.");
        problem.setProperty("detalhes", invalidFields);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler({InvalidRequestException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class})
    public ProblemDetail handleBadRequest(Exception ex) {
        String detail = ex instanceof InvalidRequestException
                ? ex.getMessage()
                : "O corpo ou os parâmetros da requisição são inválidos.";
        return problem(HttpStatus.BAD_REQUEST, "Requisição inválida", detail);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ProblemDetail> handleRateLimit(RateLimitExceededException ex) {
        long retryAfter = Math.max(1, java.time.Duration.between(OffsetDateTime.now(ZoneOffset.UTC).toInstant(),
                ex.getResetEm()).toSeconds());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter))
                .header("X-RateLimit-Limit", String.valueOf(ex.getLimite()))
                .header("X-RateLimit-Remaining", "0")
                .header("X-RateLimit-Reset", String.valueOf(ex.getResetEm().getEpochSecond()))
                .body(problem(HttpStatus.TOO_MANY_REQUESTS, "Limite de requisições excedido", ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String titulo = status == HttpStatus.UNAUTHORIZED ? "Não autorizado" : "Erro na requisição";
        String detalhe = ex.getReason() == null ? "A requisição não pôde ser processada." : ex.getReason();
        return problem(status, titulo, detalhe);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        LOGGER.error("Erro não tratado ao processar requisição", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro interno ao processar a requisição.");
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://api.pdv-loja.local/errors/" + status.value()));
        problem.setProperty("mensagem", detail);
        problem.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC).toString());
        problem.setProperty("detalhes", Collections.emptyMap());
        return problem;
    }
}
