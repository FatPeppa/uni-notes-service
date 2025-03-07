package org.skyhigh.notesservice.model.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.skyhigh.notesservice.validation.annotation.Email;
import org.skyhigh.notesservice.validation.annotation.NotEmpty;
import org.skyhigh.notesservice.validation.annotation.Size;

@Data
@Schema(name = "Запрос на аутентификацию", description = "Тело запроса на аутентификацию пользователя")
public class SignInRequest {

    @Schema(description = "Имя пользователя. От 5 до 30 символов", example = "Jon")
    @Size(min = "5", max = "30")
    private String username;

    @Schema(description = "Адрес электронной почты. От 5 до 255 символов", example = "test-user1@gmail.com")
    @Size(min = "5", max = "255")
    @Email
    private String email;

    @Schema(description = "Пароль. От 8 до 30 символов", example = "my_1secret1_password")
    @Size(min = "8", max = "30")
    @NotEmpty
    private String password;
}