package org.skyhigh.notesservice.data.dto.authentication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.skyhigh.notesservice.validation.annotation.Email;
import org.skyhigh.notesservice.validation.annotation.NotEmpty;
import org.skyhigh.notesservice.validation.annotation.Size;

@Data
@Schema(name = "Запрос на регистрацию", description = "Тело запроса на регистрацию")
public class SignUpRequest {

    @Schema(description = "Имя пользователя. От 5 до 30 символов", example = "TestUser1")
    @Size(min = "5", max = "30")
    @NotEmpty
    private String username;

    @Schema(description = "Адрес электронной почты. От 5 до 255 символов", example = "test-user1@gmail.com")
    @Size(min = "5", max = "255")
    @NotEmpty
    @Email
    private String email;

    @Schema(description = "Пароль. От 8 до 30 символов", example = "my_1secret1_password")
    @Size(max = "30")
    @NotEmpty
    private String password;
}
