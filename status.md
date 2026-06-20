# Routines App Status

This document tracks the progress of the Routines app development. AI agents should update this document regularly when completing milestones.

## Phase 1: Planning and Setup (✅ In Progress)
- [x] Initial requirements defined (`init.md`)
- [x] Android best practices grounding document created (`android_best_practices.md`)
- [x] Wireframes generated for core screens (`wireframes.md`)
- [x] Android project initialized via Android CLI
- [x] Room Database dependencies added
- [x] Room Entities, DAOs, and Database classes created
- [x] Repository Layer Implementation
- [x] Dependency Injection (Hilt) Configuration

## Phase 2: Core Data & Business Logic (✅ Completed)
- [x] ViewModels for Routines and Tasks setup
- [x] UI State management implementation

## Phase 3: UI Implementation (✅ Completed)
- [x] Home Screen UI
- [x] Create Routine Screen UI
- [x] Running Routine Screen UI
- [x] Premium Design System Setup (Material 3 Dynamic Colors)

## Phase 4: Polish & Integration (✅ Completed)
- [x] Screen transitions — `AnimatedContent` slide + fade between all screens
- [x] Drag-and-drop task reordering — handle drag in `CreateRoutineScreen` swaps tasks in real time
- [x] Google Account Sync future-proofing — `lastModifiedAt` on both entities; Room migration 1→2

## Phase 5: Bugs & Improvements for Phases:1-4 (✅ Completed)
- [x] Home screen: tapping routine name opens edit mode; play button (▶) on each card starts the routine
- [x] Skipping or finishing the last task now shows a "Routine Complete 🎉" screen with a Done button
- [x] Duration picker now includes 30s and 1m presets, plus a "Custom" chip that reveals min/sec text fields
- [x] Tapping the icon circle slides up a categorized bottom sheet (6 categories × 10 icons)
- [ ] For each task, remove the chips and let the user choose the time.
