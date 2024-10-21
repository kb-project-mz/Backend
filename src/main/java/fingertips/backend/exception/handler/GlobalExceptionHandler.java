package fingertips.backend.exception.handler;

import fingertips.backend.exception.dto.ErrorResponse;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.exception.error.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<JsonResponse<ErrorResponse>> handleApplicationException(ApplicationException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getCode());
        JsonResponse<ErrorResponse> response = JsonResponse.failure(errorResponse);
        return new ResponseEntity<>(response, e.getStatus());
    }
}
