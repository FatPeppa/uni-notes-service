package org.skyhigh.notesservice.data.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ с токенами доступа")
public class AuthenticationResponse {
    @Schema(description = "access-токен")
    private String accessToken;

    @Schema(description = "Срок жизни access-токена")
    private String accessTokenExpiry;

    @Schema(description = "refresh-токен")
    private String refreshToken;

    @Schema(description = "Срок жизни refresh-токена")
    private String refreshTokenExpiry;
}
