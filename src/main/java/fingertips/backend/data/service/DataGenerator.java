package fingertips.backend.data.service;

import fingertips.backend.data.dto.DataDTO;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataGenerator {

    private final Faker faker;

    public List<DataDTO> generateTransactions(int count) {
        List<DataDTO> transactions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DataDTO transaction = new DataDTO();
            transaction.setAccountIdx(faker.number().numberBetween(1, 3));
//            transaction.setAccountTransactionDate(faker.date().past(30, java.util.concurrent.TimeUnit.DAYS).toInstant()
//                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate());

            // 날짜 오늘만임
            transaction.setAccountTransactionDate(LocalDate.now());

            // 시간은 랜덤으로
            transaction.setAccountTransactionTime(faker.date().past(1, java.util.concurrent.TimeUnit.HOURS).toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalTime());

//            transaction.setAccountTransactionType(faker.bool().bool() ? "입금" : "출금");

            // 입금, 출금 랜덤으로 되게
            String transactionType = faker.bool().bool() ? "입금" : "출금";
            transaction.setAccountTransactionType(transactionType);

            // 입금, 출금에 따라
            int amount;
            if ("입금".equals(transactionType)) {
                amount = faker.number().numberBetween(1, 5000)*10;
            } else {
                amount = -faker.number().numberBetween(1, 5000)*10;
            }
            transaction.setAmount(amount);


            transaction.setCategoryIdx(14);
//            transaction.setCategoryIdx(faker.number().numberBetween(12, 14));
//            transaction.setAccountTransactionDescription(faker.name().fullName());
//            transaction.setAmount(faker.number().numberBetween(-50000, 50000));

            //account_transaction
//            String koreanName = faker.name().fullName().replace(" ", "");
//            transaction.setAccountTransactionDescription(koreanName);

            //card_transaction
            String companyName = faker.company().name();
            String productName = faker.commerce().productName();
            String paymentDescription = companyName + " - " + productName;
            transaction.setAccountTransactionDescription(paymentDescription);

            transactions.add(transaction);

        }
        return transactions;
    }
}
