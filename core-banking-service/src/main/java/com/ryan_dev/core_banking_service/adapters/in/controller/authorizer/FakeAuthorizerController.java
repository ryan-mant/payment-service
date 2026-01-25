package com.ryan_dev.core_banking_service.adapters.in.controller.authorizer;

import com.ryan_dev.core_banking_service.adapters.out.client.authorizer.response.AuthorizerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fake-authorizer")
public class FakeAuthorizerController {

    @PostMapping
    public ResponseEntity<AuthorizerResponse> authorize() {
        return ResponseEntity.ok(new AuthorizerResponse("AUTHORIZED", "Authorization successful"));
    }
}