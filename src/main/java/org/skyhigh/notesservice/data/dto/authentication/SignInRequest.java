package org.skyhigh.notesservice.data.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.skyhigh.notesservice.validation.annotation.NotEmpty;
import org.skyhigh.notesservice.validation.annotation.Size;

@Data
@Schema(name = "Запрос на аутентификацию", description = "Тело запроса на аутентификацию пользователя")
public class SignInRequest {

    @Schema(description = "Имя пользователя. От 5 до 30 символов", example = "Jon")
    @Size(min = "5", max = "30")
    @NotEmpty
    private String username;

    @Schema(description = "Пароль. От 8 до 30 символов", example = "my_1secret1_password")
    @Size(min = "8", max = "30")
    @NotEmpty
    private String password;
}