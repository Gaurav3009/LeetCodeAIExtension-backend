package com.extension.leetcodeSolutionExplaination.Dto;

public record RequestSolDto(
        String problemName,   //Problem name
        String problem,       // Problem Description
        String code,          // Solution
        String language,      // Coding language
        String githubToken    // GitHub Token
) {
}
