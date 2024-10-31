package fingertips.backend.carddata.service;

import fingertips.backend.carddata.dto.CardDataDTO;
import fingertips.backend.carddata.mapper.CardDataMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardDataService {

    private final CardDataGenerator cardDataGenerator;
    private final CardDataMapper cardDataMapper;

    @Transactional
    public void generateAndSaveCardData(int count) {
        List<CardDataDTO> transactions = cardDataGenerator.generateCardData(count);
        for (CardDataDTO transaction : transactions) {
            cardDataMapper.insertCardData(transaction);
        }
    }
}
