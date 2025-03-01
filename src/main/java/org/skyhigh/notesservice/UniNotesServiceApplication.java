package org.skyhigh.notesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UniNotesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniNotesServiceApplication.class, args);
    }

}
