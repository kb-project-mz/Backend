package fingertips.backend.carddata.service;

import fingertips.backend.carddata.dto.CardDataDTO;
import fingertips.backend.carddata.mapper.CardDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardDataService {

    private final CardDataGenerator cardDataGenerator;
    private final CardDataMapper cardDataMapper;

    @Transactional
    public void generateAndSaveCardData(int count) {
        List<CardDataDTO> transactions = cardDataGenerator.generateCardData(count);
        transactions.forEach(transaction -> {
            cardDataMapper.insertCardData(transaction);
        });
    }
}
