package org.skyhigh.notesservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.skyhigh.notesservice.data.dto.system.TokensTtl;
import org.skyhigh.notesservice.service.system.SystemParametersServiceImpl;
import org.skyhigh.notesservice.validation.aspect.ValidParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/v1")
@RequiredArgsConstructor
@Tag(name = "Системные методы")
public class SystemController {
    private final SystemParametersServiceImpl systemParametersService;

    @Operation(summary = "Регистрация пользователя")
    @GetMapping(value = "/tokens-ttl", produces = "application/json")
    @ValidParams
    public ResponseEntity<TokensTtl> getTokensTtl() {
        return ResponseEntity.ok(systemParametersService.getTokensTtl());
    }

    //TO DO: метод получения максимальных размеров и кол-ва файлов
}
