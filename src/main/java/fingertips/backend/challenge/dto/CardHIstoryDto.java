package fingertips.backend.challenge.dto;

import lombok.Data;

@Data
public class CardHIstoryDto
{
    Integer id;
    Integer card_id;
    String consumption_date;
    String consumption_time;
    Integer category;
    String content;
    Integer amount;
}
