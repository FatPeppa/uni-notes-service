package org.skyhigh.notesservice.data.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело ответа по установленным ограничениям времени жизни токенов")
public class TokensTtl {
    @Schema(description = "Время жизни access токена в минутах")
    private Integer accessTokenTtl;

    @Schema(description = "Время жизни refresh токена в часах")
    private Integer refreshTokenTtl;
}
