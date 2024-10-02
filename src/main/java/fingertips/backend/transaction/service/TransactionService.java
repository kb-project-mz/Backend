package fingertips.backend.transaction.service;

import fingertips.backend.transaction.dto.AccountTransactionDTO;
import fingertips.backend.transaction.dto.CardTransactionDTO;
import fingertips.backend.transaction.dto.PeriodDTO;

import java.util.List;

public interface TransactionService {

    List<CardTransactionDTO> getCardTransactionList(Integer memberId);
    List<CardTransactionDTO> getCardTransactionListByPeriod(PeriodDTO period);
    String getMostAndMaximumUsed(PeriodDTO period);
    List<AccountTransactionDTO> getAccountTransactionList(Integer memberId);
    String getAiRecommendation(PeriodDTO periodDTO);
}
