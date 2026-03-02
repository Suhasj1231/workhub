package com.smj.workhub.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(

        @NotBlank(message = "Project name is required")
        @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
        String name,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
) {}