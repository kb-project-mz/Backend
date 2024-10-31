package fingertips.backend.carddata.service;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;
import fingertips.backend.carddata.dto.CardDataDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class CardDataGenerator {

    private final Faker faker = new Faker();
    private final Random random = new Random();

    // 브랜드와 카테고리를 매핑하는 Map
    private final Map<String, Integer> brandCategoryMap = Map.of(
            "스타벅스", 2,
            "매머드커피", 2,
            "쿠팡", 10,
            "배달의민족", 1,
            "APPLE", 11,
            "파리바게뜨", 1,
            "CGV", 5,
            "NIKE", 7,
            "올리브영", 4,
            "무신사", 7
    );

    public List<CardDataDTO> generateCardData(int count) {
        List<CardDataDTO> transactions = new ArrayList<>();

        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 30);
        long days = ChronoUnit.DAYS.between(startDate, endDate);

        for (int i = 0; i < count; i++) {
            LocalDate randomDate = startDate.plusDays(ThreadLocalRandom.current().nextLong(days + 1));
            LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60));

            String randomBrand = brandCategoryMap.keySet().stream()
                    .skip(random.nextInt(brandCategoryMap.size()))
                    .findFirst()
                    .orElse("스타벅스"); // 기본값으로 "스타벅스" 설정
            int categoryIdx = brandCategoryMap.get(randomBrand);
            int amount = faker.number().numberBetween(10, 300) * 100;
            CardDataDTO transaction = CardDataDTO.builder()
                    .cardIdx(faker.number().numberBetween(1, 5)) // 카드 ID 범위
                    .cardTransactionDate(randomDate)
                    .cardTransactionTime(randomTime)
                    .categoryIdx(categoryIdx)
                    .cardTransactionDescription(randomBrand)
                    .amount(amount)
                    .build();

            transactions.add(transaction);
        }
        return transactions;
    }
}
