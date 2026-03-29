package com.extension.leetcodeSolutionExplaination.services;

import com.extension.leetcodeSolutionExplaination.Constants.PromptConstants;
import com.extension.leetcodeSolutionExplaination.Dto.RequestSolDto;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GenAIService {

    private final ChatClient chatClient;
    private final GitHubService gitHubService;

    public String generateSolution(RequestSolDto requestSolDto) {
//        Prepare the prompt
        String userPrompt = String.format(
                PromptConstants.USER_PROMPT_TEMPLATE,
                requestSolDto.language(),
                requestSolDto.code(),
                requestSolDto.problem()
        );
//        Pass the prompt to GenAI and receive the resposne
        String response = chatClient.prompt()
                .system(PromptConstants.SYSTEM_ROLE)
                .user(userPrompt)
                .call()
                .content();
//        Push the explanation to the GitHub repository
        String username = gitHubService.getUsername(requestSolDto.githubToken());
        gitHubService.pushExplanation(
                requestSolDto.githubToken(),
                username,
                requestSolDto.problemName(),
                response
        );
        return response;
    }



}
