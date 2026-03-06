# WorkHub

WorkHub is a backend system for managing collaborative workspaces,
projects, and tasks.

The system organises work into a hierarchical structure:

Workspace → Project → Task

It provides REST APIs for managing workspaces, organizing projects,
tracking tasks, and filtering work efficiently. The application is
designed with production-grade backend practices including layered
architecture, DTO-based APIs, soft deletes, filtering, pagination, and
database indexing.

------------------------------------------------------------------------

# Features

Workspace Management - Create, update, list, delete workspaces - Soft
delete and restore functionality - Pagination and filtering support

Project Management - Projects belong to workspaces - Unique project
names within a workspace - Soft delete and restore support - Filtering
and pagination

Task Management - Tasks belong to projects - Status workflow
management - Priority levels - Due dates - Filtering, sorting, and
pagination

Backend Features - Layered architecture (Controller → Service →
Repository) - DTO-based API responses - Global exception handling -
Validation using Jakarta Validation - Dynamic filtering using Spring
Data Specifications - Soft delete support - Composite database
indexing - Logging for service operations - Swagger/OpenAPI API
documentation

------------------------------------------------------------------------

# Tech Stack

Backend - Java 21 - Spring Boot - Spring Data JPA - Hibernate

Database - PostgreSQL

Build Tool - Gradle

Documentation - Swagger / OpenAPI

Version Control - Git - GitHub

------------------------------------------------------------------------

# System Architecture

The application follows a layered architecture.

Controller Layer\
Handles HTTP requests and responses.\
Responsible for request validation and returning DTO responses.

Service Layer\
Contains business logic and orchestration of application behavior.

Repository Layer\
Handles database access using Spring Data JPA.

Entity Layer\
Represents database tables and relationships.

DTO Layer\
Defines API request and response contracts to prevent exposing internal
entities.

------------------------------------------------------------------------

# Database Schema

The system contains three main entities.

Workspace - id - name - description - deleted - created_at - updated_at

Project - id - workspace_id (FK) - name - description - deleted -
created_at - updated_at

Task - id - project_id (FK) - title - description - status - priority -
due_date - deleted - created_at - updated_at

Relationships

Workspace → One-to-Many → Project\
Project → One-to-Many → Task

------------------------------------------------------------------------

# Database Indexing

Indexes are created based on query patterns to improve filtering and
pagination performance.

Workspace

PRIMARY KEY(id)\
UNIQUE(name)\
INDEX(is_deleted)

Project

PRIMARY KEY(id)\
UNIQUE(workspace_id, name)\
INDEX(workspace_id, deleted, created_at)

Task

PRIMARY KEY(id)\
INDEX(project_id, deleted, created_at)

------------------------------------------------------------------------

# API Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

------------------------------------------------------------------------

# Workspace APIs

Create Workspace\
POST /api/v1/workspaces

Get All Workspaces\
GET /api/v1/workspaces

Get Workspace by ID\
GET /api/v1/workspaces/{id}

Update Workspace\
PUT /api/v1/workspaces/{id}

Delete Workspace\
DELETE /api/v1/workspaces/{id}

Restore Workspace\
PATCH /api/v1/workspaces/{id}/restore

------------------------------------------------------------------------

# Project APIs

Create Project under Workspace\
POST /api/v1/workspaces/{workspaceId}/projects

List Projects of Workspace\
GET /api/v1/workspaces/{workspaceId}/projects

Update Project\
PUT /api/v1/projects/{projectId}

Delete Project\
DELETE /api/v1/projects/{projectId}

Restore Project\
PATCH /api/v1/projects/{projectId}/restore

------------------------------------------------------------------------

# Task APIs

Create Task under Project\
POST /api/v1/projects/{projectId}/tasks

List Tasks under Project\
GET /api/v1/projects/{projectId}/tasks

Get Task by ID\
GET /api/v1/tasks/{taskId}

Update Task\
PUT /api/v1/tasks/{taskId}

Update Task Status\
PATCH /api/v1/tasks/{taskId}/status

Delete Task\
DELETE /api/v1/tasks/{taskId}

------------------------------------------------------------------------

# Running the Application Locally

Clone the repository

git clone git@github.com:SuhasMj/workhub.git

Navigate into project

cd workhub

Create PostgreSQL database

CREATE DATABASE workhub;

Create database user

CREATE USER workhub_user WITH PASSWORD 'your_password';

GRANT ALL PRIVILEGES ON DATABASE workhub TO workhub_user;

Configure environment variable

export DB_PASSWORD=your_password

Run the application

./gradlew bootRun

The application will start at

http://localhost:8080

------------------------------------------------------------------------

# Example API Request

Create Workspace

POST /api/v1/workspaces

Request Body

{ "name": "Engineering", "description": "Engineering workspace" }

Response

{ "id": 1, "name": "Engineering", "description": "Engineering
workspace", "createdAt": "...", "updatedAt": "..." }

------------------------------------------------------------------------

# Design Decisions

DTO Usage\
Entities are not exposed directly through APIs to maintain separation
between persistence and API layers.

Soft Deletes\
Entities use a boolean deleted flag to allow restoration and historical
tracking.

Specifications for Filtering\
Spring Data JPA Specifications are used to implement dynamic filtering.

Composite Indexing\
Indexes are designed based on query patterns to optimize filtering and
pagination.

Layered Architecture\
Separates responsibilities between controllers, services, and
repositories.


------------------------------------------------------------------------

# Author

Suhas MJ

GitHub\
https://github.com/SuhasMj
