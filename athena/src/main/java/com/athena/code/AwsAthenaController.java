package com.athena.code;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AwsAthenaController {
 
    private final AwsAthenaService athenaService;

    @PostMapping("/query")
    public String executeQuery(@RequestBody BodyRequest body) {
        return athenaService.executeQuery(body.getQuery());
    }
      

}

