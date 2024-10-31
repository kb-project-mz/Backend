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
            "쿠팡", 10, // 생활품
            "배달의민족", 1, // 식비
            "APPLE", 11, // 미분류
            "파리바게뜨", 1,
            "CGV", 5, // 취미
            "NIKE", 7,
            "올리브영", 4, // 미용
            "무신사", 7 // 의류
    );

    public List<CardDataDTO> generateCardData(int count) {
        List<CardDataDTO> transactions = new ArrayList<>();

        // 2024년 11월의 시작과 끝 날짜를 정의
        LocalDate startDate = LocalDate.of(2024, 11, 1);
        LocalDate endDate = LocalDate.of(2024, 11, 30);
        long days = ChronoUnit.DAYS.between(startDate, endDate);

        for (int i = 0; i < count; i++) {
            // 2024년 11월 중 랜덤 날짜 생성
            LocalDate randomDate = startDate.plusDays(ThreadLocalRandom.current().nextLong(days + 1));
            // 랜덤 시간 생성 (0~23시, 0~59분 중 랜덤)
            LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60));

            // 랜덤으로 브랜드 선택
            String randomBrand = brandCategoryMap.keySet().stream()
                    .skip(random.nextInt(brandCategoryMap.size()))
                    .findFirst()
                    .orElse("스타벅스"); // 기본값으로 "스타벅스" 설정

            // 선택된 브랜드에 따른 카테고리 ID 가져오기
            int categoryIdx = brandCategoryMap.get(randomBrand);

            // 거래 금액 설정 (100의 배수)
            int amount = faker.number().numberBetween(10, 1000) * 100;

            CardDataDTO transaction = CardDataDTO.builder()
                    .cardIdx(faker.number().numberBetween(1, 5)) // 카드 ID 범위
                    .cardTransactionDate(randomDate) // 2024년 11월 중 랜덤 날짜
                    .cardTransactionTime(randomTime) // 랜덤 시간 설정
                    .categoryIdx(categoryIdx) // 브랜드에 맞는 카테고리 설정
                    .cardTransactionDescription(randomBrand) // 브랜드 이름만 설명에 설정
                    .amount(amount) // 100의 배수로 설정된 거래 금액
                    .build();

            transactions.add(transaction);
        }
        return transactions;
    }
}
