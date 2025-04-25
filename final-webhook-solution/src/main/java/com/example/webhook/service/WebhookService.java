package com.example.webhook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

    private final MutualFollowersService mutualFollowersService;
    private final NthLevelFollowersService nthLevelFollowersService;

    @Value("${questionType}")
    private int questionType;

    public WebhookService(MutualFollowersService mfs, NthLevelFollowersService nfs) {
        this.mutualFollowersService = mfs;
        this.nthLevelFollowersService = nfs;
    }

    public void execute() {
        if (questionType == 1) {
            mutualFollowersService.solve();
        } else {
            nthLevelFollowersService.solve();
        }
    }
}
