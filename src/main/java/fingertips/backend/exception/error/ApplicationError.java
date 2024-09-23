package fingertips.backend.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApplicationError {

    // 토큰, 권한
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "COMMON_001", "토큰이 유효하지 않습니다."),
    AUTHORIZATION_DENIED(HttpStatus.UNAUTHORIZED, "COMMON_002", "권한이 부족합니다."),
    AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED, "COMMON_003", "인증에 실패했습니다."),


    // 로그인
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "존재하지 않는 회원입니다."),
    MEMBER_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_002", "존재하지 않는 아이디입니다."),
    PASSWORD_INVALID(HttpStatus.NOT_FOUND, "MEMBER_003", "패스워드가 틀렸습니다."),
    LOGIN_ATTEMPTS(HttpStatus.BAD_REQUEST, "MEMBER_004", "로그인 시도가 초과되었습니다."),

    // 회원가입
    MEMBER_ID_DUPLICATED(HttpStatus.BAD_REQUEST, "MEMBER_005", "이미 존재하는 아이디입니다."),
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "MEMBER_006", "이미 존재하는 이메일입니다."),



    // 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_001", "서버 내부 에러가 발생하였습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}