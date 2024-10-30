package fingertips.backend.data.service;

import fingertips.backend.data.dto.DataDTO;
import fingertips.backend.data.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {
    private final DataGenerator dataGenerator;
    private final DataMapper dataMapper;

    @Transactional
    public void generateAndSaveTransactions(int count) {
        List<DataDTO> results = dataGenerator.generateTransactions(count);
        results.forEach(result -> {
            dataMapper.insertData(result);
//            log.info("Inserted transaction: {}", transaction);
        });
    }
}
