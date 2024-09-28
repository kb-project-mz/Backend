package fingertips.backend.challenge.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardHistoryDTO {

    Integer id;
    Integer card_id;
    String consumption_date;
    String consumption_time;
    Integer category;
    String content;
    Integer amount;
}
