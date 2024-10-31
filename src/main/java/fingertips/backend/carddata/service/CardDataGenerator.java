package fingertips.backend.carddata.service;
import fingertips.backend.carddata.dto.CardDataDTO;
import org.springframework.stereotype.Component;
import java.util.*;
import net.datafaker.Faker;



@Component
public class CardDataGenerator {

    private final Faker faker = new Faker();
    private final Random random = new Random();

    // 한국 브랜드 이름 리스트
    private final List<String> brandNames = List.of(
            "스타벅스", "빽다방", "매머드커피", "투썸플레이스", "이디야커피",
            "삼성전자", "LG전자", "카카오", "네이버", "현대자동차",
            "SK텔레콤", "쿠팡", "배달의민족", "APPLE", "롯데백화점",
            "하이마트", "파리바게뜨", "던킨도너츠", "CGV", "메가박스",
            "아디다스", "나이키", "뉴발란스", "ABC마트", "올리브영"
    );

    public List<CardDataDTO> generateCardData(int count) {
        List<CardDataDTO> transactions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CardDataDTO transaction = CardDataDTO.builder()
                    .cardTransactionId(faker.number().numberBetween(1, 3))
                    .cardIdx(faker.number().numberBetween(1, 100)) // 카드 ID 범위
                    .cardTransactionDate(faker.date().past(30, java.util.concurrent.TimeUnit.DAYS).toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate())
                    .cardTransactionTime(faker.date().past(1, java.util.concurrent.TimeUnit.HOURS).toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalTime())
                    .categoryIdx(faker.number().numberBetween(1, 20)) // 카테고리 ID 범위
                    .cardTransactionDescription(faker.company().name() + " - " + faker.commerce().productName())
                    .amount(faker.number().numberBetween(1000, 50000))
                    .build();

            transactions.add(transaction);
        }
        return transactions;
    }
}
