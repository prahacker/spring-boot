# Spring Boot Webhook Auto-Caller

This project is part of the **Bajaj Finserv Health Programming Challenge**. It automatically calls a remote API on application startup, solves the assigned problem, and posts the result to a webhook with JWT authorization.

---

##  Participant Details

- **Name**: Prakhar Tripathi  
- **Reg. No**: RA2211030010002  
- **Email**: pt7920@srmist.edu.in

---

##  What It Does

On application startup, the app:
1. Sends a POST request to `/generateWebhook` to receive a webhook URL, access token, and user data.
2. Based on the response structure, it:
   - Solves **Question 1** if users is a flat list (mutual followers).
   - Solves **Question 2** if users is nested (Nth-level followers).
3. Posts the solution JSON to the webhook URL with JWT authentication.
4. Retries up to 4 times if the webhook fails.

---

##  How to Run Question 1 or Question 2

The app **auto-detects** the question type based on the response format. Just ensure the correct `regNo` is used:

| Reg No Ending | Triggered Question | Description              |
|---------------|--------------------|--------------------------|
| Odd (e.g. `7`) | Question 1         | Mutual followers logic   |
| Even (e.g. `2`) | Question 2         | Nth-level followers logic|

To test a specific question:
- Open `MutualFollowersService.java` and `NthLevelFollowersService.java`
- Set your `name`, `email`, and `regNo` accordingly
- Run the app — it will auto-select the logic

---

##  Question 1: Mutual Followers

### Input:
```json
[
  { "id": 1, "follows ": [2] },
  { "id": 2, "follows ": [1] }
]
