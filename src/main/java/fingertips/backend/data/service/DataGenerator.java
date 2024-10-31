package fingertips.backend.data.service;

import fingertips.backend.data.dto.DataDTO;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;


@Component
@RequiredArgsConstructor
public class DataGenerator {

    private final Faker faker;
    private final Random random = new Random();

    // 한국 브랜드 이름 리스트
    private final List<String> brandNames = List.of(
            "스타벅스", "빽다방", "매머드커피", "투썸플레이스", "이디야커피",
            "삼성전자", "LG전자", "카카오", "네이버", "현대자동차",
            "SK텔레콤", "쿠팡", "배달의민족", "신라면세점", "롯데백화점",
            "하이마트", "파리바게뜨", "던킨도너츠", "CGV", "메가박스",
            "아디다스", "나이키", "뉴발란스", "ABC마트", "올리브영"
    );

    public List<DataDTO> generateTransactions(int count) {
        List<DataDTO> transactions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DataDTO transaction = new DataDTO();
            transaction.setAccountIdx(faker.number().numberBetween(1, 3));
            transaction.setAccountTransactionDate(LocalDate.now());
            transaction.setAccountTransactionTime(faker.date().past(1, java.util.concurrent.TimeUnit.HOURS).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalTime());

            String transactionType = faker.bool().bool() ? "입금" : "출금";
            transaction.setAccountTransactionType(transactionType);

            int amount;
            if ("입금".equals(transactionType)) {
                amount = faker.number().numberBetween(1, 5000) * 10;
            } else {
                amount = -faker.number().numberBetween(1, 5000) * 10;
            }
            transaction.setAmount(amount);

            transaction.setCategoryIdx(14);

            // 브랜드 이름을 무작위로 선택
            String brand = brandNames.get(random.nextInt(brandNames.size()));
            String productName = faker.commerce().productName();
            String paymentDescription = brand + " - " + productName;
            transaction.setAccountTransactionDescription(paymentDescription);

            transactions.add(transaction);
        }
        return transactions;
    }
}