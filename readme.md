# Server Backup Management System

## 🚀 Project Overview

This is a comprehensive server backup and monitoring system designed to provide system administrators with a centralized, visual, and intelligent management platform. The project utilizes a modern front-end/back-end separated architecture, with the backend built on Spring Boot and the frontend on Vue 3.

It implements a range of core features, including data source management, scheduled backups, system metrics monitoring, email alerts, capacity prediction, and an AI assistant. This project serves as an excellent example for course assignments or final year projects, covering the full software engineering lifecycle from requirement analysis and technology selection to deployment.

## ✨ Key Features

*   **Login Module**: Provides a secure access point to the system.
*   **Backup Module**:
    *   Card-based management for various data sources (MySQL, Oracle, Mongo).
    *   Full CRUD (Create, Read, Update, Delete) operations for data sources.
    *   Supports both on-demand and scheduled backup tasks for specific data sources.
*   **System Dashboard**:
    *   A dynamic and visually appealing dashboard for real-time monitoring of core system status.
    *   Displays real-time statistics: total backup tasks, successes, failures, and scheduled tasks.
*   **Alert Module**:
    *   Configuration switches for failure alerts via email and SMS (reserved).
    *   Automatically sends alert emails to administrators upon backup task failure.
*   **Metrics Monitoring**:
    *   Fetches key performance indicators (CPU, Memory, Disk I/O, etc.) from target servers in real-time via SSH.
    *   Utilizes ECharts for data visualization and supports switching between different servers.
*   **Capacity Prediction**:
    *   Predicts when server storage capacity will be exhausted based on historical backup data using a linear regression algorithm.
    *   Provides intuitive predictions and charts based on actual server capacity data.
*   **AI Assistant**:
    *   Simulates an interactive experience with a large language model, featuring a typewriter-style streaming output.
    *   Offers pre-set, professional answers to specific questions.

## 🛠️ Tech Stack

*   **Backend**:
    *   **Core Framework**: Spring Boot
    *   **Persistence**: MyBatis-Plus
    *   **Database**: MySQL 5.7
    *   **Task Scheduling**: Quartz
    *   **Mail Service**: Spring Mail
    *   **Remote Connection**: JSch (for SSH)
*   **Frontend**:
    *   **Core Framework**: Vue 3 (Composition API)
    *   **Build Tool**: Vite
    *   **UI Framework**: Element Plus
    *   **Charting Library**: ECharts
    *   **Routing**: Vue Router
    *   **State Management**: Pinia (reserved)
    *   **HTTP Client**: Axios
*   **Deployment & DevOps**:
    *   **Containerization**: Docker
    *   **Version Control**: Git & GitHub


