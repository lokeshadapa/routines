# Routines App: Android Best Practices and Ground Rules

This document outlines the core principles, architecture, and tools to be used by AI agents when building the Routines Android App.

## 1. UI / Presentation Layer
* **Framework:** Jetpack Compose (Single Activity Architecture).
* **Design System:** Material Design 3 (M3). Ensure support for dynamic colors, dark/light themes, and modern interactive elements to make the app feel premium.
* **State Management:** Use Compose state holding techniques (`StateFlow` from ViewModels).

## 2. Architecture
* **Pattern:** Model-View-ViewModel (MVVM) coupled with the Repository Pattern.
* **Separation of Concerns:** Keep UI code in Compose, business logic in ViewModels, and data operations in Repositories.

## 3. Data Storage & Sync
* **Local First:** The app requires no login to start using it. All data (routines, tasks, statistics) must be stored locally using **Room Database**.
* **Future Cloud Backup:** Data will be backed up nightly to the user's Google Account (similar to WhatsApp backups). Design the database and repositories with this eventual synchronization in mind.

## 4. Development Workflow & Android CLI
* **Tooling:** Agents **MUST** use the provided `android-cli` tool/skill for creating the project, managing the SDK, running builds, and diagnostics.
* **Language:** Kotlin (100%).
* **Asynchronous Programming:** Use Kotlin Coroutines and `Flow` for all background tasks and database observations.
* **Dependency Injection:** Use **Hilt** to inject dependencies into ViewModels and Repositories.

## 5. Agent Instructions
* Always refer to this document before making structural changes.
* Prioritize premium aesthetics in the UI—avoid basic or generic layouts.
* Keep the data structure flexible so that adding new task types to a routine is seamless.
