# 🤖 LeetCode AI Explainer — Spring Boot Backend

> AI-powered LeetCode solution explanation service built with Spring Boot and Google Gemini. Generates detailed explanations for your code solutions and automatically pushes them as Markdown files to your GitHub repository.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [GitHub Integration](#github-integration)
- [Future SaaS Roadmap](#future-saas-roadmap)

---

## Overview

This Spring Boot service acts as the **AI gateway** between the LeetCode AI Explainer Chrome Extension and Google Gemini. It receives your LeetCode solution, generates a detailed explanation using Gemini AI, and automatically saves the explanation as a `.md` file in your GitHub repository.

### What it does:
- Accepts LeetCode solution details from the Chrome Extension
- Calls **Google Gemini 2.5 Flash** to generate a human-readable explanation
- Authenticates with GitHub using your Personal Access Token (PAT)
- Creates the `leetcode-solution-explanations` repo if it doesn't exist
- Pushes `problem-name.md` with the explanation content
- Updates the file if the explanation already exists (re-explain support)

---

## Architecture

```
Chrome Extension
      │
      │  POST /chatAsk/ask
      │  { language, code, problem, problemName, githubToken }
      ▼
┌─────────────────────────────────────────────────────┐
│              Spring Boot Backend                    │
│                                                     │
│  GenAIController                                    │
│       │                                             │
│       ▼                                             │
│  GenAIService                                       │
│  ├── ChatClient (Spring AI)                         │
│  │     └── Calls Gemini 2.5 Flash API    ──────────►│ Google Gemini
│  │           Returns explanation         ◄──────────│
│  │                                                  │
│  └── GitHubService                                  │
│        ├── GET  /user          (fetch username)     │
│        ├── GET  /repos/:user   (check repo exists)  │
│        ├── POST /user/repos    (create if missing)  │
│        └── PUT  /contents/:file (push .md file) ───►│ GitHub API
│                                                     │
└─────────────────────────────────────────────────────┘
      │
      │  Returns explanation text
      ▼
Chrome Extension (displays result + shows GitHub link)
```

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.4.1 | Application framework |
| Spring AI | 1.1.4 | Gemini AI integration |
| Google Gemini | 2.5 Flash | LLM for explanations |
| Spring Web | 6.2.1 | REST API |
| Lombok | 1.18.36 | Boilerplate reduction |
| Maven | 3.x | Build tool |

---

## Project Structure

```
src/
└── main/
    └── java/
        └── com/extension/leetcodeSolutionExplaination/
            │
            ├── LeetcodeSolutionExplainationApplication.java   ← Main class
            │
            ├── config/
            │   ├── AppConfig.java          ← ChatClient + RestTemplate beans
            │   └── CorsConfig.java         ← CORS for Chrome Extension
            │
            ├── controller/
            │   └── GenAIController.java    ← POST /chatAsk/ask
            │
            ├── services/
            │   ├── GenAIService.java       ← Gemini call + orchestration
            │   └── GitHubService.java      ← GitHub API integration
            │
            ├── Dto/
            │   └── RequestSolDto.java      ← Request body record
            │
            └── Constants/
                └── PromptConstants.java    ← System + user prompt templates
```

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- Google Gemini API key → [Get one here](https://aistudio.google.com/apikey)
- GitHub Personal Access Token → [Generate here](https://github.com/settings/tokens/new?scopes=repo,read:user)

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/leetcode-solution-explanations-backend.git
cd leetcode-solution-explanations-backend
```

### 2. Set environment variables
```bash
# Windows (PowerShell)
$env:GEMINI_API_KEY="AIza_your_gemini_key_here"

# Mac/Linux
export GEMINI_API_KEY=AIza_your_gemini_key_here
```

### 3. Run the application
```bash
mvn spring-boot:run
```

Server starts on `http://localhost:8081`

### 4. Verify it's running
```bash
curl http://localhost:8081/actuator/health
# {"status":"UP"}
```

---

## Configuration

### `application.properties`
```properties
spring.application.name=leetcodeSolutionExplaination

# Server
server.port=8081

# Gemini AI — key injected from environment variable
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-2.5-flash
spring.ai.google.genai.chat.options.temperature=0.5
```

### Environment Variables

| Variable | Description | Required |
|---|---|---|
| `GEMINI_API_KEY` | Your Google Gemini API key | ✅ Yes |

> ⚠️ **Never hardcode your API key in `application.properties`** — always use environment variables to prevent accidental exposure in version control.

---

## API Reference

### `POST /chatAsk/ask`

Generates an AI explanation for a LeetCode solution and pushes it to GitHub.

**Request Body:**
```json
{
  "language":    "Java",
  "code":        "public int[] twoSum(int[] nums, int target) { ... }",
  "problem":     "Given an array of integers, return indices of two numbers that add up to target.",
  "problemName": "Two Sum",
  "githubToken": "ghp_your_personal_access_token"
}
```

| Field | Type | Description |
|---|---|---|
| `language` | `String` | Programming language of the solution |
| `code` | `String` | The actual solution code |
| `problem` | `String` | Problem description (auto-detected from LeetCode) |
| `problemName` | `String` | Problem name used as the `.md` filename |
| `githubToken` | `String` | GitHub PAT with `repo` + `read:user` scopes |

**Response:**
```
200 OK
Content-Type: text/plain

## Two Sum — Explanation

### Approach
This solution uses a HashMap to store...
...
```

**Error Responses:**

| Status | Reason |
|---|---|
| `500` | Gemini API error / quota exceeded |
| `500` | Invalid GitHub token |
| `500` | GitHub API rate limit |

---

## GitHub Integration

The `GitHubService` handles all GitHub operations automatically:

### 1. Fetch Username
```
GET https://api.github.com/user
Authorization: Bearer {githubToken}
```

### 2. Check if repo exists
```
GET https://api.github.com/repos/{username}/leetcode-solution-explanations
```

### 3. Create repo if missing
```
POST https://api.github.com/user/repos
Body: { "name": "leetcode-solution-explanations", "auto_init": true }
```

### 4. Push explanation file
```
PUT https://api.github.com/repos/{username}/leetcode-solution-explanations/contents/{problem-name}.md
Body: { "message": "Add explanation: Two Sum", "content": "<base64 encoded>" }
```

> If the file already exists, the service fetches its `sha` and sends an **update** request instead of a create — so re-explaining the same problem overwrites the old file cleanly.

---

## Future SaaS Roadmap

This project is currently built for **personal use**. Here's the planned evolution into a SaaS product:

### Phase 1 — Auth & User Management
- [ ] Replace GitHub PAT with **JWT-based authentication**
- [ ] User signup / login (email or GitHub OAuth)
- [ ] Store users in PostgreSQL

### Phase 2 — Usage & Billing
- [ ] Free tier: 10 explanations/day
- [ ] Track usage per user in database
- [ ] Integrate **Stripe** for paid plans
- [ ] Rate limiting per subscription tier

### Phase 3 — Features
- [ ] Explanation history per user
- [ ] Multiple AI model support (GPT-4, Claude)
- [ ] Custom prompt templates
- [ ] Solution complexity analysis (Time + Space)
- [ ] Multi-language explanation (explain in Hindi, Spanish etc.)

### Phase 4 — Scale
- [ ] Move from single instance to containerized deployment (Docker + K8s)
- [ ] Redis caching for repeated problems
- [ ] CDN for static assets
- [ ] Monitoring with Grafana + Prometheus

---

## Local Development Tips

### Skip tests for faster builds
```bash
mvn clean install -DskipTests
```

### Test the endpoint with curl
```bash
curl -X POST http://localhost:8081/chatAsk/ask \
  -H "Content-Type: application/json" \
  -d '{
    "language": "Java",
    "code": "public int[] twoSum(int[] nums, int target) { return new int[]{}; }",
    "problem": "Return indices of two numbers that add up to target.",
    "problemName": "Two Sum",
    "githubToken": "ghp_your_token_here"
  }'
```

### IntelliJ HTTP Client (`.http` file)
```http
POST http://localhost:8081/chatAsk/ask
Content-Type: application/json

{
  "language": "Java",
  "code": "public int[] twoSum(int[] nums, int target) { return new int[]{}; }",
  "problem": "Return indices of two numbers that add up to target.",
  "problemName": "Two Sum",
  "githubToken": "ghp_your_token_here"
}
```

---

## License

MIT License — free to use, modify, and distribute.

---

> Built with ☕ Java + Spring Boot | Powered by Google Gemini 2.5 Flash
