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

    // 탈퇴
    MEMBER_WITHDRAWN(HttpStatus.BAD_REQUEST, "MEMBER_010", "탈퇴한 회원입니다. 다시 회원가입을 해주세요."),

    // 마이페이지
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER_007", "비밀번호가 일치하지 않습니다."),
    PASSWORD_CORRESPOND(HttpStatus.BAD_REQUEST, "MEMBER_008", "이전과 같은 비밀번호는 사용하실 수 없습니다."),
    INVALID_NEW_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER_009", "비밀번호가 입력되지 않았습니다."),

    // 이메일 인증
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "EMAIL_001", "유효하지 않은 인증 코드입니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL_002", "인증 코드가 만료되었습니다."),
    EMAIL_SENDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_003", "이메일 전송에 실패하였습니다."),

    // 소셜 로그인
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, "SOCIAL_001", "유효하지 않은 ID 토큰입니다."),
    TOKEN_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SOCIAL_002", "토큰 검증에 실패하였습니다."),
    USER_INFO_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SOCIAL_003", "사용자 정보를 요청하는 데 실패했습니다."),
    SOCIAL_LOGIN_INTEGRATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SOCIAL_004", "소셜 로그인 연동에 실패했습니다."),
    OAUTH2_AUTHORIZATION_FAILED(HttpStatus.UNAUTHORIZED, "SOCIAL_005", "OAuth2 권한 부여에 실패했습니다."),
    SOCIAL_LOGIN_SESSION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SOCIAL_006", "소셜 로그인 세션 생성에 실패했습니다."),
    SOCIAL_ACCOUNT_DISCONNECT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SOCIAL_007", "소셜 계정 연동 해제에 실패했습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "SOCIAL_008", "유효하지 않은 액세스 토큰입니다."),
    INVALID_USER_INFO(HttpStatus.BAD_REQUEST, "SOCIAL_009", "유효하지 않은 사용자 정보입니다."),
    SOCIAL_ENCODING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"SOCIAL_010", "인코딩 처리 중 오류가 발생했습니다."),

    // 서버
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_001", "서버 내부 에러가 발생하였습니다."),

    // 데이터베이스
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_001", "데이터베이스 처리 중 오류가 발생했습니다."),

    // 파일 업로드
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "FILE_001", "파일이 비어 있습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_002", "파일 업로드에 실패하였습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_003", "파일 삭제에 실패하였습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_004", "파일을 찾을 수 없습니다."),

    // 스케줄러
    USER_METRICS_NOT_FOUND(HttpStatus.NOT_FOUND, "METRICS_001", "사용자 메트릭을 찾을 수 없습니다."),
    USER_METRICS_INSERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "METRICS_002", "사용자 메트릭 삽입에 실패하였습니다."),
    USER_METRICS_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "METRICS_003", "사용자 메트릭 업데이트에 실패하였습니다."),
    USER_METRICS_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "METRICS_004", "사용자 메트릭 삭제에 실패하였습니다."),
    USER_METRICS_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "METRICS_005", "메트릭 데이터를 저장하는 데 실패했습니다."),
    SCHEDULER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SCHEDULER_001", "스케줄러 실행에 실패했습니다."),

    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "REDIS_001", "레디스 통신 중 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
