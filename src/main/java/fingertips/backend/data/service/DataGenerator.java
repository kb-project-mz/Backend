package fingertips.backend.data.service;

import fingertips.backend.data.dto.DataDTO;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataGenerator {

    private final Faker faker;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public List<DataDTO> generateTransactions(int count) {
        List<DataDTO> members = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DataDTO member = new DataDTO();
            // 이름
            String koreanName = faker.name().fullName().replace(" ", "");
            member.setMemberName(koreanName);

            //password
            String encodedPassword = passwordEncoder.encode("Test123!");
            member.setPassword(encodedPassword);

            // memberId: 영어 단어 + 숫자 조합 (랜덤)
            member.setMemberId(generateMemberId());

            // birthday
            Date rawBirthday = faker.date().birthday(15, 45); // 18세에서 60세 사이의 생일
            LocalDate formattedBirthday = rawBirthday.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            member.setBirthday(formattedBirthday);

            // 성별
            String memberGender = faker.bool().bool() ? "F" : "M";
            member.setGender(memberGender);

            //이메일
//            member.setEmail(faker.internet().emailAddress());
            String emailPrefix = faker.letterify("??????") + faker.numerify("##");
            String domain = faker.options().option("naver.com", "daum.net", "gmail.com", "yahoo.com");
            String email = emailPrefix + "@" + domain;
            member.setEmail(email);

            // is_active
            member.setIsActive(1);

            // join_date
            member.setJoinDate(LocalDate.now());

            // role
            member.setRole("ROLE_USER");

            // is_login_locked
            member.setIsLoginLocked(0);

            // login_lock_time
            member.setLoginLockTime(0);

            members.add(member);

        }
        return members;
    }

    private String generateMemberId() {
        // 랜덤 단어 생성
        String word = faker.letterify("??????"); // 랜덤한 6글자 영어 단어
        String number = faker.numerify("###");  // 3자리 숫자

        // 랜덤으로 단어 뒤에 숫자를 붙이거나 조합
        if (random.nextBoolean()) {
            return word + number; // 단어 + 숫자
        } else {
            return word + faker.letterify("????"); // 단어 + 랜덤 알파벳 4글자
        }
    }
}
