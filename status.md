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
- [x] Routine-level icon selector: mint circle with pencil in name card opens emoji picker; saved to RoutineEntity.icon (migration 4→5); home card uses routine icon with first-task fallback.
- [x] Navbar: labels removed, icons 28dp, Crossfade(260ms) for fill/outline swap, M3 sliding indicator pill for horizontal tab-switch motion.
- [x] Home: removed quote card and "YOUR ROUTINES" section label + divider; widened headline underline to 72dp/3dp; count badge moved to top-right of Routines header row.
- [x] Navbar: Tomato-style island — two NavPill composables in a floating pill Row; selected tab fills orange circle and expands label text via expandHorizontally; unselected shows only icon in SubText.
- [x] CLAUDE.md updated: Tomato app at /Users/lokesh/Documents/Tomato added as the gold standard reference for all UI/UX decisions.
- [x] Running routine timer: animateFloatAsState(tween 800ms) for smooth arc progress; ring enlarged to 280dp/18dp stroke; clock font 64sp Black with tight letter-spacing.
- [ ] I need to work on improving the navbar spring motion right now it contracts after I change it to a new page
- [ ] And also when I open a routine that has already been saved from the homepage instead of playing it it loses all the data like the task data icon data etc but however when I play it the task is still intact so even when I open it in edit mode it should show up whatever is being saved ready to fix that
- [ ] lets move to Inter font, first design and map which font style and size each component we have will use then implement the font change. I downlaoded the whole font lib here - /Users/lokesh/Downloads/Inter. update the /Users/lokesh/Documents/routines/design_handoff_routines_ui_redesign/Design System.dc.html as well. update that we also move to mint theme from burntorange
