package org.skyhigh.notesservice.controller;

import org.skyhigh.notesservice.data.dto.TestDTO;
import org.skyhigh.notesservice.validation.aspect.ValidParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/api")
public class TestController {

    @ValidParams(flks = {"Flk1000000"})
    @GetMapping
    ResponseEntity<?> getAuthorities(@RequestBody TestDTO testDTO) {
        /*log.info("initiated getAuthorities: " + login);
        AuthorityDTO result = restAuthorityService.getAuthorities(login);
        if (result.getAuthorities() == null || result.getAuthorities().isEmpty())
            log.info("getAuthorities: no authorities found for login: " + login);
        log.info("getAuthorities: found " + result.getAuthorities().size() + " authorities for login: " + login);
        return ResponseEntity.ok(result);*/
        //throw new NullPointerException();
        return null;
    }
}
