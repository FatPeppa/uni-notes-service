package org.skyhigh.notesservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common/v1")
@RequiredArgsConstructor
@Tag(name = "Общие методы")
public class CommonController {
}
