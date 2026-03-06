//package com.smj.workhub.project.dto;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//
//public record CreateProjectRequest(
//
//        @NotBlank(message = "Project name is required")
//        @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
//        String name,
//
//        @Size(max = 500, message = "Description cannot exceed 500 characters")
//        String description
//) {}


package com.smj.workhub.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for creating a new project inside a workspace")
public record CreateProjectRequest(

        @Schema(
                description = "Project name (must be unique inside a workspace)",
                example = "Payment Service"
        )
        @NotBlank(message = "Project name must not be blank")
        @Size(min = 3, max = 150, message = "Project name must be between 3 and 150 characters")
        String name,

        @Schema(
                description = "Optional project description",
                example = "Handles all payment related services"
        )
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
) {}







