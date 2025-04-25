package com.example.webhook.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class NthLevelFollowersService {
    private final RestTemplate restTemplate = new RestTemplate();

    public void solve() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";

        Map<String, String> request = Map.of(
                "name", "John Doe",
                "regNo", "RA2211030010002",
                "email", "john@example.com"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        String webhook = (String) response.getBody().get("webhook");
        String token = (String) response.getBody().get("accessToken");
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");

        Map<String, Object> userGraph = (Map<String, Object>) data.get("users");
        int n = (int) userGraph.get("n");
        int findId = (int) userGraph.get("findId");
        List<Map<String, Object>> usersRaw = (List<Map<String, Object>>) userGraph.get("users");

        Map<Integer, List<Integer>> followMap = new HashMap<>();
        for (Map<String, Object> u : usersRaw) {
            int id = (int) u.get("id");
            List<Integer> follows = (List<Integer>) u.get("follows ");
            followMap.put(id, follows);
        }

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(findId);
        visited.add(findId);

        int level = 0;
        while (!queue.isEmpty() && level < n) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int curr = queue.poll();
                for (int next : followMap.getOrDefault(curr, List.of())) {
                    if (!visited.contains(next)) {
                        queue.add(next);
                        visited.add(next);
                    }
                }
            }
            level++;
        }

        List<Integer> outcome = new ArrayList<>(queue);
        Collections.sort(outcome);

        Map<String, Object> resultMap = Map.of(
                "regNo", "RA2211030010002",
                "outcome", outcome
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
