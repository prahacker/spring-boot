package com.example.webhook.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class MutualFollowersService {
    private final RestTemplate restTemplate = new RestTemplate();

    public void solve() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";

        Map<String, String> request = Map.of(
                "name", "Prakhar Tripathi",
                "regNo", "RA2211030010002",
                "email", "pt7920@srmist.edu.in"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        String webhook = (String) response.getBody().get("webhook");
        String token = (String) response.getBody().get("accessToken");
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");

        Object usersObj = data.get("users");
        List<Map<String, Object>> usersRaw = new ArrayList<>();

        if (usersObj instanceof List<?> usersList) {
            for (Object obj : usersList) {
                if (obj instanceof Map<?, ?> map) {
                    usersRaw.add((Map<String, Object>) map);
                }
            }
        } else if (usersObj instanceof Map<?, ?> nested) {
            Object innerUsers = nested.get("users");
            if (innerUsers instanceof List<?> innerList) {
                for (Object obj : innerList) {
                    if (obj instanceof Map<?, ?> map) {
                        usersRaw.add((Map<String, Object>) map);
                    }
                }
            } else {
                throw new RuntimeException("Expected 'users' inside nested structure to be a list.");
            }
        } else {
            throw new RuntimeException("Unexpected 'users' format.");
        }

        Map<Integer, Set<Integer>> followMap = new HashMap<>();
        for (Map<String, Object> map : usersRaw) {
            int id = (int) map.get("id");
            List<Integer> follows = (List<Integer>) map.get("follows ");
            if (follows == null) follows = new ArrayList<>();
            followMap.put(id, new HashSet<>(follows));
        }

        Set<List<Integer>> result = new HashSet<>();
        for (int id : followMap.keySet()) {
            for (int f : followMap.get(id)) {
                if (followMap.containsKey(f) && followMap.get(f).contains(id)) {
                    List<Integer> pair = Arrays.asList(Math.min(id, f), Math.max(id, f));
                    result.add(pair);
                }
            }
        }

        Map<String, Object> resultMap = Map.of(
                "regNo", "RA2211030010002",
                "outcome", new ArrayList<>(result)
        );

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<Map<String, Object>> postEntity = new HttpEntity<>(resultMap, headers);
        for (int i = 0; i < 4; i++) {
            try {
                restTemplate.postForEntity(webhook, postEntity, String.class);
                break;
            } catch (Exception e) {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        }
    }
}
