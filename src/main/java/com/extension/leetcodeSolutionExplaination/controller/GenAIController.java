package com.extension.leetcodeSolutionExplaination.controller;

import com.extension.leetcodeSolutionExplaination.Dto.RequestSolDto;
import com.extension.leetcodeSolutionExplaination.services.GenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chatAsk")
@RequiredArgsConstructor
public class GenAIController {

    private final GenAIService genAIService;
/*
    Endpoint to generate the explanation for a LeetCode solution
*/
    @PostMapping("/ask")
    public String explainSolution(@RequestBody RequestSolDto requestSolDto) {
        return genAIService.generateSolution(requestSolDto);
    }

}
