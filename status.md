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
- [x] Bouncy spring press animation on Pause, Skip, Done, Create Routine FAB, Save Routine, Add Task — scale to 92% on press, spring back with DampingRatioMediumBouncy on release
- [x] Day-of-week schedule picker on Create/Edit routine — 7 circular day chips (M T W T F S S), bitmask stored in Room (migration 2→3), selected days shown in orange on Home routine cards
- [x] Reminder toggle + time picker on Create/Edit routine — Switch enables reminder, Material3 clock-face TimePicker sets time; AlarmManager schedules exact alarms per selected day-of-week (self-rescheduling weekly via BroadcastReceiver); `POST_NOTIFICATIONS` runtime permission requested at launch; `USE_EXACT_ALARM` auto-granted on Android 13+; Room migration 3→4 for reminderEnabled/Hour/Minute; 17 unit tests covering day calculation, bitmask, and alarm code uniqueness
- [x] Running routine notification progress bar — ongoing notification appears when a routine starts, shows routine name + current task + task progress bar (X of N); updates on each task advance; auto-cancels when routine completes or screen is closed. Uses a silent low-importance channel so it never interrupts.
- [x] Drag-and-drop now has full visual feedback: dragged card scales up 4% with a spring animation, gets a warm orange background tint, a subtle orange border, and the drag handle turns BurntOrange; a ↑/↓ direction arrow appears in the handle column once drag crosses 35% of the swap threshold
- [x] Quote typewriter: removed stray closing quote during animation; added blinking `_` cursor that moves with text and disappears when typing completes; closing quote appended only after full text is shown. "YOUR ROUTINES" divider now spans full width including under the count badge.
- [x] Navbar island: floating pill shape with shadow, proper bottom padding eliminates the sliver; outlined icon when unselected, filled+mint when selected.
- [x] Delete routine button: red circle background removed; plain outlined trash icon in SubText gray.
- [x] Delete button in edit mode: outlined trash icon + "Delete" text in red, no background circle.
- [x] Navbar: Crossfade animation (220ms) on icon switch; selected tab shows ExtraBold label (unselected Medium) so both text and icon feel selected together.
- [x] FAB: changed from extended "Create Routine" pill to compact circle with just +; moved up to bottom=150dp so it clears the navbar island.
- [x] Home routine cards: first task emoji shown in a mint-tint circle; duration and task count displayed as mint-colored pills matching the Explore page style.
- [ ] Now I understand the problem when we are actually saving a routine next to the routine name there is no icon to choose so add an icon selector to a routine and then when the user saves that routine show that icon in the home page
- [ ] And for the navbar remove the text home and explore just keep the icons Since the text is going away, I think you can increase the size of the icons And also when it moves from home to explore or explore to home Make it look like it's sliding create that motion
- [ ] Remove the quote section and so I think you can also remove your routines and The underline as well. Just add that big underline under the main routines headline itself.
- [ ] Verify the font that is used by the tomato app. The folder is at /Users/lokesh/Documents/Tomato. I think we will use that font and also the sizing everything etc. We'll also use whatever this app is using for each element So let me know the font I will download it and we can use it and just see what icons it is using We'll use the same icons as well. Let me know if you need the icons as well.
- [ ] And for the navbar as well we'll exactly use the island format that the tomato app uses and we'll do the same transitions and the motions that it has.
- [ ] Going forward for every change in every UI/UX decision, we will first refer to this tomato app So add this reference in cloud.md to check in tomato app every time you make a decision.This app is the gold standard in terms of UI, transitions, timer, notifications and mostly everything related to design and UI.
- [ ] Also for the Routine runner page, match the timer size to exactly what it is in the tomato app with the font, the circle size and the flow, how smooth it is, match it similar to what it is in the tomato app
