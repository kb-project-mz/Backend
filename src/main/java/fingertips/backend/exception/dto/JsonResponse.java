package fingertips.backend.exception.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.security.account.dto.AuthDTO;
import lombok.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JsonResponse<T> {

    private boolean success;
    private T data;
    private ErrorResponse error;

    public static void sendError(HttpServletResponse response, ApplicationError error) throws IOException {

        ObjectMapper om = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        Writer out = response.getWriter();

        ErrorResponse errorResponse = new ErrorResponse(error.getMessage(), error.getCode());
        out.write(om.writeValueAsString(JsonResponse.failure(errorResponse)));
        out.flush();
    }

    public static void sendToken(HttpServletResponse response, AuthDTO authDTO) throws IOException {

        ObjectMapper om = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        Writer out = response.getWriter();
        out.write(om.writeValueAsString(JsonResponse.success(authDTO)));
        out.flush();
    }

    public static <T> JsonResponse<T> success(T data) {
        return JsonResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static JsonResponse<ErrorResponse> failure(ErrorResponse error) {
        return JsonResponse.<ErrorResponse>builder()
                .success(false)
                .error(error)
                .build();
    }
}
