# Handoff: Routines App — UI Redesign

## Overview

This is a complete UI redesign of the **Routines** Android app — a habit/routine builder where users create timed task sequences and run them. The app has three main screens (Home, Create Routine, Running Routine) plus two supporting surfaces (Icon Picker bottom sheet, Empty State).

This redesign replaces the default Material 3 dynamic-color/purple theme with a custom design system: **Nunito typeface**, **Burnt Orange accent**, and **Warm White backgrounds**.

---

## About the Design Files

The `.dc.html` files in this bundle are **high-fidelity design references created in HTML** — they show the intended look, layout, and interaction states with pixel-level precision. They are **not production code**.

Your task is to **recreate these designs in the existing Android codebase** (Kotlin + Jetpack Compose + Material 3), using the project's established MVVM/Repository/Room/Hilt architecture. The HTML prototypes should be opened in a browser for visual reference.

**Fidelity: High-fidelity.** Colors, typography, spacing, radius, and component treatments should match the mockups closely. Every exact value is documented in this README and in `Design System.dc.html`.

---

## Implementation Task List (Ordered)

See `Phase 6 - UI Implementation Tasks.md` for the **full step-by-step task list** with Kotlin code snippets. The correct sequence is:

1. Backend changes (new DAO query, session tracking)
2. Theme (Nunito font, Color.kt, Type.kt, Theme.kt)
3. Home Screen
4. Create Routine Screen
5. Running Routine Screen
6. Icon Picker
7. Empty State
8. Housekeeping

---

## Design Tokens

### Colors

| Token | Hex | Kotlin | Usage |
|-------|-----|--------|-------|
| BurntOrange | `#D4773E` | `Color(0xFFD4773E)` | Primary accent, FAB, buttons, ring, badges |
| BurntOrangeLight | `#F7EDE5` | `Color(0xFFF7EDE5)` | Tinted surfaces: quote card, icon circles, secondary button bg |
| WarmWhite | `#F7F6F3` | `Color(0xFFF7F6F3)` | Screen background |
| CardWhite | `#FFFFFF` | `Color(0xFFFFFFFF)` | Card surfaces |
| NearBlack | `#0F0F14` | `Color(0xFF0F0F14)` | Primary text, section rules |
| SubText | `#ABABAB` | `Color(0xFFABABAB)` | Metadata, labels, captions |
| BorderLight | `#EBEBEB` | `Color(0xFFEBEBEB)` | Dividers, list separators |
| NeutralPill | `#EEEDE9` | `Color(0xFFEEEDE9)` | Collapsed duration pill bg, back button bg |

### Typography — Nunito (Google Fonts)

All text uses **Nunito**. Load via `androidx.compose.ui:ui-text-google-fonts`.

| Role | Size | Weight | Letter Spacing | Usage |
|------|------|--------|---------------|-------|
| displayLarge | 48sp | Black (900) | -2sp | Timer countdown number |
| headlineLarge | 32sp | ExtraBold (800) | -0.5sp | Screen title ("Routines") |
| headlineMedium | 22sp | ExtraBold (800) | -0.5sp | Current task name, completion heading |
| titleLarge | 17sp | Bold (700) | -0.3sp | Nav bar title, card names |
| bodyLarge | 14sp | Regular (400) *italic* | 0 | Quote card text |
| bodyMedium | 13sp | SemiBold (600) | 0 | Sub-text, empty state copy |
| labelLarge | 10sp | ExtraBold (800) | 2sp | Section headers (ALL CAPS) |
| labelSmall | 10sp | Bold (700) | 1.5sp | Metadata: "45 MIN · 4 TASKS" |

### Radius

| Token | Value | Usage |
|-------|-------|-------|
| RadiusCard | 18dp | Routine cards, task cards |
| RadiusPill | 28dp | FAB, bottom sheets (top corners) |
| RadiusButton | 16dp | Primary/secondary buttons |
| RadiusInput | 14dp | Name input card, add task button |
| RadiusBadge | 50% | Count badges, icon circles, nav buttons |
| RadiusPillSmall | 20dp | Duration pills, category tabs |
| RadiusQuote | 16dp | Quote card |

### Elevation / Shadow

Cards: `elevation = 2.dp` — generates a subtle shadow (~`0 2px 10px rgba(0,0,0,0.07)`).
No tonal color surfaces (disable `tonalElevation`).

### Spacing

| Value | Usage |
|-------|-------|
| 10dp | Gap between cards in list |
| 12dp | Gap between cards (running screen) |
| 20dp | Horizontal padding (create/running screens) |
| 24dp | Horizontal padding (home screen) |
| 28dp | Section header top margin |
| 30dp | Screen content top padding (below nav bar) |

---

## Screen 1 — Home Screen (`HomeScreen.kt`)

**Reference:** `Home Screen.dc.html`

### Layout
Full-screen `Box`. Inside: a `Column` (scrollable content) + `ExtendedFloatingActionButton` pinned `Alignment.BottomEnd` with `padding(bottom=24.dp, end=20.dp)`.

### Nav Bar
None — the screen starts with the header directly.

### Header
```
padding(top=30.dp, horizontal=24.dp)
Text("Routines", headlineLarge, NearBlack)
Box(width=48.dp, height=2.dp, color=NearBlack, margin-top=10.dp)
```

### Quote Card
```
margin(top=22.dp, horizontal=20.dp)
Card(shape=RoundedCornerShape(16.dp), containerColor=BurntOrangeLight, elevation=0.dp)
  padding(16.dp/18.dp)
  Text(quote, bodyLarge italic, color=Color(0xFF5A2A10))
  minLines=2, animates letter by letter via LaunchedEffect + delay(40ms)
```

### Section Header
```
margin(top=28.dp, horizontal=24.dp)
Row(fillMaxWidth, SpaceBetween, verticalAlignment=CenterVertically)
  padding-bottom=10.dp, border-bottom=Divider(2.dp, NearBlack)
  Text("YOUR ROUTINES", labelLarge, NearBlack, uppercase)
  Box(22.dp circle, BurntOrange) { Text(routines.size, 11sp ExtraBold, White) }
```

### Routine Card (RoutineCard composable)
```
Card(18.dp, elevation=2.dp, containerColor=CardWhite)
  padding(18.dp)
  Row(fillMaxWidth, SpaceBetween, CenterVertically)
    Column
      Text(routine.name, titleLarge Bold, NearBlack)
      Text("${taskCount} tasks · ${formatDuration(totalDuration)}", labelSmall, SubText)
    Box(38.dp circle, BurntOrange)  ← play button
      Icon(PlayArrow, White, 12.dp)
```

Wire task metadata: `viewModel.getTaskSummary(routine.id).collectAsStateWithLifecycle()`

Tap left side → `onEditRoutineClick(routine.id)`
Tap play button → `onPlayRoutineClick(routine.id)`

### FAB (Extended)
```
ExtendedFloatingActionButton(
  text = { Text("Create Routine", 14sp ExtraBold, White) },
  icon = { Icon(Icons.Filled.Add, White, 16.dp) },
  shape = RoundedCornerShape(28.dp),
  containerColor = BurntOrange,
  onClick = onCreateRoutineClick,
  modifier = Modifier.padding(bottom=24.dp, end=20.dp)
)
```

---

## Screen 2 — Empty State (within HomeScreen.kt)

**Reference:** `Empty State.dc.html`

Show when `routines.isEmpty()`. Replace the card list + section header with `EmptyStateContent()`.

### EmptyStateContent composable
```
Column(horizontalAlignment=CenterHorizontally, padding=horizontal 24.dp)

  // Ghost card stack
  Box(height=136.dp, fillMaxWidth)
    // Back card (lowest)
    Card(18.dp, alpha=0.05f, fillMaxWidth padding 20.dp, align=BottomCenter, height=64.dp)
    // Mid card
    Card(18.dp, alpha=0.08f, fillMaxWidth padding 10.dp, offset-bottom=12.dp, height=64.dp)
    // Front card (white, full opacity, with skeleton lines)
    Card(18.dp, elevation=2.dp, fillMaxWidth, offset-bottom=24.dp, height=68.dp)
      Row(padding=horizontal 18.dp, CenterVertically, gap=14.dp)
        Box(38.dp circle, NeutralPill)   ← ghost icon
        Column
          Box(height=12.dp, width=55%, bg=NeutralPill, radius=6.dp)
          Box(height=9.dp, width=30%, bg=BurntOrangeLight, radius=5.dp, margin-top=7.dp)
        Box(36.dp circle, NeutralPill)   ← ghost play button

  Spacer(36.dp)
  Text("No routines yet", headlineMedium, NearBlack, textAlign=Center)
  Spacer(10.dp)
  Text("Build your first routine and start making your days count.", bodyMedium, SubText, textAlign=Center)
  Spacer(20.dp)
  // Down arrow hint
  Text("tap the button below to begin", labelSmall, SubText/60%)
  Icon(Icons.Rounded.KeyboardArrowDown, SubText, 20.dp)
```

---

## Screen 3 — Create Routine Screen (`CreateRoutineScreen.kt`)

**Reference:** `Create Routine.dc.html` (frame 1 = default, frame 2 = duration picker open)

### Layout
`Box(fillMaxSize)` — Column content + fixed save bar at bottom.

### Custom Nav Bar
```
Row(fillMaxWidth, padding=16.dp/20.dp, border-bottom=Divider(1.dp, BorderLight))
  Box(36.dp circle, NeutralPill, clickable=onBack)
    Icon(ArrowBack, NearBlack, 18.dp)
  Text("New Routine", titleLarge ExtraBold, NearBlack, margin-start=12.dp)
```

### Routine Name Input
```
margin(top=18.dp, horizontal=20.dp)
Card(14.dp, elevation=2.dp, containerColor=CardWhite)
  Column(padding=14.dp/16.dp)
    Text("ROUTINE NAME", labelSmall, SubText, uppercase)
    BasicTextField(routineName, textStyle=titleLarge, NearBlack, cursorColor=BurntOrange)
```

### Tasks Section Header
Same pattern as Home Screen: `"TASKS"` + count badge.

### DraftTaskCard (redesigned)
```
Card(18.dp, elevation=2.dp, containerColor=CardWhite)
  padding(14.dp/16.dp)
  Row(fillMaxWidth, CenterVertically, gap=10.dp)
    Icon(DragHandle, CACACA, modifier=pointerInput drag gesture)
    Box(38.dp circle, BurntOrangeLight, clickable=showIconPicker)
      Text(task.icon, 20sp)
    BasicTextField(task.name, Modifier.weight(1f), titleMedium Bold)
    // Duration pill
    Surface(shape=RoundedCornerShape(20.dp), color=NeutralPill,
            modifier=Modifier.clickable { showDurationPicker = true })
      Row(padding=5.dp/12.dp, CenterVertically, gap=5.dp)
        Text(formatDuration(task.durationSeconds), 13sp ExtraBold, NearBlack)
        Icon(KeyboardArrowDown, SubText, 10.dp)
    // Delete button
    Box(28.dp circle, Color(0xFFFEE4E4), clickable=onDelete)
      Icon(Close, Color(0xFFE53E3E), 12.dp)
```

**Remove entirely:** `DURATION_PRESETS`, all `FilterChip` rows, `showCustom` state, `customMinsText`/`customSecsText` state.

### Duration Picker Bottom Sheet
Trigger: `showDurationPicker = true`

```
ModalBottomSheet(shape=RoundedCornerShape(topStart=28.dp, topEnd=28.dp))
  Column(padding=horizontal 16.dp)

    // Handle
    Box(40.dp × 4.dp, Color(0xFFE0E0E0), radius=2.dp, align=CenterHorizontally, padding-top=12.dp)

    // Header
    Row(padding=6.dp/20.dp/14.dp, SpaceBetween)
      Text("Set Duration", titleLarge ExtraBold)
      Surface(radius=20.dp, BurntOrangeLight) { Text(task.name, 12sp Bold, BurntOrange) }

    // Time display: HH : MM : SS
    Row(justify=Center, padding=14.dp/16.dp, align=Baseline)
      TimeUnit(hours, "hr")
      Text(":", 42sp ExtraBold, SubText)
      TimeUnit(minutes, "min")
      Text(":", 42sp ExtraBold, SubText)
      TimeUnit(seconds, "sec")
    // Each TimeUnit:
    //   Row(align=Baseline, gap=4.dp)
    //     Text(value, 42sp ExtraBold, NearBlack)
    //     Text(label, 13sp Bold, SubText, margin-bottom=6.dp)

    // Numpad
    LazyVerticalGrid(Fixed(3), gap=8.dp, padding=horizontal 16.dp)
      // Keys 1-9
      keys.forEach { NumpadKey(it) }
      // Last row: empty, 0, backspace
      Spacer(); NumpadKey("0"); BackspaceKey()
    // NumpadKey:
    //   Surface(radius=14.dp, WarmWhite, height=52.dp, fillMaxWidth)
    //     Text(digit, 22sp Bold, NearBlack, align=Center)
    // BackspaceKey:
    //   Surface(radius=14.dp, BurntOrangeLight, height=52.dp)
    //     Icon(Backspace, BurntOrange)

    // Entry logic:
    //   var buffer by remember { mutableStateOf("000000") }  // 6 chars: HHMMSS
    //   On digit press: buffer = (buffer + digit).takeLast(6)
    //   On backspace: buffer = "0" + buffer.dropLast(1)
    //   Compute: hh=buffer[0..1].toInt(), mm=buffer[2..3].toInt(), ss=buffer[4..5].toInt()
    //   durationSeconds = hh*3600 + mm*60 + ss
    //   Display: hours=hh, minutes=mm, seconds=ss

    // Confirm button
    Button(
      containerColor=BurntOrange, shape=RoundedCornerShape(16.dp),
      modifier=Modifier.fillMaxWidth().height(52.dp).padding(16.dp),
      onClick = { onTaskChange(task.copy(durationSeconds=computed)); showDurationPicker=false }
    ) { Text("Confirm", 16sp ExtraBold, White) }
```

### Add Task Button
```
OutlinedButton(
  shape=RoundedCornerShape(14.dp),
  border=BorderStroke(1.5.dp, BorderLight),  // add dashed PathEffect for visual
  modifier=Modifier.fillMaxWidth().padding(top=12.dp, horizontal=20.dp)
)
Row(CenterVertically, gap=8.dp)
  Box(22.dp circle, BurntOrange) { Icon(Add, White, 12.dp) }
  Text("Add Task", 14sp Bold, BurntOrange)
```

### Fixed Save Button
```
Box(fillMaxSize) {
  // Gradient fade behind button
  Box(Modifier.align(BottomCenter).fillMaxWidth().height(96.dp),
      background=Brush.verticalGradient([Transparent, WarmWhite]))
  Button(
    containerColor=BurntOrange, shape=RoundedCornerShape(16.dp),
    modifier=Modifier.align(BottomCenter).fillMaxWidth()
                     .padding(horizontal=20.dp, bottom=20.dp).height(52.dp),
    onClick = { if (routineName.isNotBlank() && draftTasks.isNotEmpty()) onSave(routineName, draftTasks) }
  ) { Text("Save Routine", 16sp ExtraBold, White) }
}
```

---

## Screen 4 — Icon Picker (IconPickerSheetContent in CreateRoutineScreen.kt)

**Reference:** `Icon Picker.dc.html` (frame 2 = Fitness active, frame 3 = Work active)

### Layout
`ModalBottomSheet` (already implemented, keep). Redesign content:

```
Column(fillMaxWidth, padding=horizontal 20.dp)

  // Handle
  Box(40.dp × 4.dp, E0E0E0, radius=2.dp, align=CenterHorizontally, padding-top=12.dp)

  // Header
  Row(padding=6.dp top, 14.dp bottom, SpaceBetween)
    Text("Choose Icon", titleLarge ExtraBold, NearBlack)
    Surface(radius=20.dp, BurntOrangeLight) { Text(task.name, 12sp Bold, BurntOrange) }

  // Category tabs
  var selectedCategory by remember { mutableStateOf(ICON_CATEGORIES[0].first) }
  LazyRow(gap=8.dp, padding-bottom=14.dp)
    ICON_CATEGORIES.forEach { (name, _) ->
      Surface(
        shape=RoundedCornerShape(20.dp),
        color = if (name == selectedCategory) BurntOrange else WarmWhite,
        modifier=Modifier.clickable { selectedCategory = name }
      )
        Text(name, 13sp Bold, if(selected) White else SubText, padding=7.dp/16.dp)

  // Divider
  Divider(1.dp, Color(0xFFF0F0EC))

  // Icon grid — filtered to selectedCategory
  val icons = ICON_CATEGORIES.first { it.first == selectedCategory }.second
  LazyVerticalGrid(Fixed(5), gap=10.dp, padding-top=16.dp/bottom=28.dp)
    icons.forEach { icon ->
      Box(
        52.dp circle,
        color = if (icon == selectedIcon) BurntOrange else NeutralPill,
        modifier = Modifier.clickable { onIconSelected(icon) }
      )
        Text(icon, 26sp)
        // If selected, add green checkmark badge:
        if (icon == selectedIcon)
          Box(18.dp circle, Color(0xFF4CAF50), border=2.dp White,
              align=TopEnd offset(-2.dp, -2.dp))
            Icon(Check, White, 9.dp)
```

---

## Screen 5 — Running Routine Screen (`RunningRoutineScreen.kt`)

**Reference:** `Running Routine.dc.html` (frame 1 = active, frame 2 = completion)

### State Additions
```kotlin
var totalElapsedSeconds by remember { mutableIntStateOf(0) }
// In timer LaunchedEffect, when playing:
totalElapsedSeconds += 1
```

### Layout
Full-screen `Column(fillMaxSize, padding=horizontal 20.dp)`.

### Custom Nav Bar
```
Row(fillMaxWidth, padding=14.dp top, border-bottom=Divider(1.dp, BorderLight))
  Box(36.dp circle, NeutralPill, clickable=onClose)
    Icon(Close, NearBlack, 14.dp)
  Text(routine.name, titleLarge ExtraBold, NearBlack, margin-start=12.dp)
```

### Progress Indicator
```
margin-top=14.dp
Row(fillMaxWidth, SpaceBetween, CenterVertically)
  Row(gap=6.dp)
    tasks.forEachIndexed { i, _ ->
      Box(24.dp × 6.dp, radius=3.dp,
          color = when {
            i < currentTaskIndex  -> BurntOrange.copy(alpha=0.4f)
            i == currentTaskIndex -> BurntOrange
            else                  -> BorderLight
          })
  Text("Task ${currentTaskIndex+1} of ${tasks.size}", labelSmall, SubText)
```

### Current Task Display
```
padding-top=22.dp
Column(CenterHorizontally, fillMaxWidth)
  Box(52.dp circle, BurntOrangeLight) { Text(currentTask.icon, 26sp) }
  Text(currentTask.name, headlineMedium, NearBlack, margin-top=10.dp)
```

### Circular Timer (Canvas)
```
// Size: 220.dp × 220.dp, centered
Canvas(Modifier.size(220.dp)) {
  val strokeWidth = 14.dp.toPx()
  val radius = (size.minDimension - strokeWidth) / 2
  val center = Offset(size.width/2, size.height/2)
  val progress = if (task.durationSeconds == 0) 1f
                 else timeRemainingSeconds / task.durationSeconds.toFloat()
  // Track
  drawArc(Color(0xFFEEEDE9), -90f, 360f, false,
          style=Stroke(strokeWidth, cap=StrokeCap.Round))
  // Progress
  drawArc(BurntOrange, -90f, 360f * progress, false,
          style=Stroke(strokeWidth, cap=StrokeCap.Round))
}
// Overlay text (use Box with contentAlignment=Center over Canvas)
Text(formatTime(timeRemainingSeconds), displayLarge 48sp, NearBlack, letterSpacing=-2.sp)
Text("remaining", labelSmall, SubText, margin-top=5.dp)
```

### Time Extender Chips
```
margin-top=16.dp
Row(justify=Center, gap=8.dp)
  listOf("+30s" to 30, "+1m" to 60, "+2m" to 120).forEach { (label, secs) ->
    SuggestionChip(
      label = { Text(label, 12sp ExtraBold, NearBlack) },
      shape = RoundedCornerShape(20.dp),
      colors = SuggestionChipDefaults.suggestionChipColors(containerColor=NeutralPill),
      onClick = { timeRemainingSeconds += secs }
    )
```

### Controls (Rectangular Buttons)
```
margin-top=28.dp
Row(justify=Center, gap=12.dp)
  // Pause/Play
  Button(
    shape=RoundedCornerShape(16.dp), containerColor=BurntOrange,
    modifier=Modifier.height(56.dp), contentPadding=PaddingValues(horizontal=28.dp)
  )
    Row(gap=10.dp, CenterVertically)
      Icon(if(isPlaying) Pause else PlayArrow, White, 18.dp)
      Text(if(isPlaying) "Pause" else "Resume", 15sp ExtraBold, White)

  // Skip
  Button(
    shape=RoundedCornerShape(16.dp), containerColor=BurntOrangeLight,
    modifier=Modifier.height(56.dp), contentPadding=PaddingValues(horizontal=24.dp)
  )
    Row(gap=8.dp, CenterVertically)
      Text("Skip", 15sp ExtraBold, BurntOrange)
      Icon(SkipNext, BurntOrange, 18.dp)
    onClick = { /* advance task */ }
```

### Up Next (single card)
```
val nextTask = tasks.getOrNull(currentTaskIndex + 1) ?: return
margin-top=24.dp
Text("UP NEXT", labelLarge, SubText, uppercase, margin-bottom=10.dp)
Card(14.dp, elevation=2.dp, containerColor=CardWhite)
  padding(14.dp/16.dp)
  Row(fillMaxWidth, CenterVertically, gap=12.dp)
    Box(38.dp circle, BurntOrangeLight) { Text(nextTask.icon, 20sp) }
    Column(Modifier.weight(1f))
      Text(nextTask.name, 15sp Bold, NearBlack)
      Text(formatTime(nextTask.durationSeconds), labelSmall, SubText, uppercase)
    Icon(ChevronRight, BorderLight, 16.dp)
```

---

## Screen 6 — Completion Screen (within RunningRoutineScreen.kt)

**Reference:** `Running Routine.dc.html` frame 2

Show when `currentTask == null && tasks.isNotEmpty()`.

```
Column(fillMaxSize, CenterHorizontally + Center, padding=horizontal 28.dp)

  // Check circle
  Box(88.dp circle, BurntOrangeLight, margin-bottom=24.dp)
    Icon(CheckCircle or custom check, BurntOrange, 40.dp)

  Text("Routine\nComplete!", headlineLarge, NearBlack, textAlign=Center, lineHeight=1.15)
  Text("${routine.name} · all done", bodyMedium, SubText, textAlign=Center, margin-top=10.dp, margin-bottom=32.dp)

  // Stats card
  Card(18.dp, elevation=2.dp, containerColor=CardWhite, fillMaxWidth, margin-bottom=28.dp)
    padding(20.dp/24.dp)
    Row(fillMaxWidth)
      Column(Modifier.weight(1f), CenterHorizontally,
             border-right=Divider(1.dp, BorderLight))
        Text(formatTime(totalElapsedSeconds), 26sp ExtraBold, NearBlack, letterSpacing=-1.sp)
        Text("TOTAL TIME", labelSmall, SubText, uppercase, margin-top=5.dp)
      Column(Modifier.weight(1f), CenterHorizontally)
        Text("${tasks.size}", 26sp ExtraBold, NearBlack, letterSpacing=-1.sp)
        Text("TASKS DONE", labelSmall, SubText, uppercase, margin-top=5.dp)

  // Quote card
  Card(14.dp, elevation=0.dp, containerColor=BurntOrangeLight, fillMaxWidth, margin-bottom=32.dp)
    padding(14.dp/18.dp)
    Text(QUOTES.random(), bodyLarge italic, Color(0xFF5A2A10), textAlign=Center)

  // Done button
  Button(containerColor=BurntOrange, shape=RoundedCornerShape(16.dp),
         modifier=Modifier.fillMaxWidth().height(52.dp))
    Text("Done", 16sp ExtraBold, White)
    onClick = onClose
```

---

## Interactions & Behavior

| Trigger | Result |
|---------|--------|
| Tap routine name (Home) | Opens CreateRoutineScreen in edit mode |
| Tap play button (Home) | Opens RunningRoutineScreen with that routine |
| Tap duration pill (Create) | Opens duration picker bottom sheet |
| Confirm duration (Create) | Closes sheet, updates task durationSeconds |
| Tap icon circle (Create) | Opens icon picker bottom sheet |
| Tap category tab (Icon Picker) | Filters icon grid, no sheet dismiss |
| Tap icon (Icon Picker) | Selects icon, shows checkmark, auto-dismisses |
| Tap +30s / +1m / +2m (Running) | Adds seconds to timeRemainingSeconds |
| Timer reaches 0 | Auto-advances to next task (existing logic, keep) |
| Last task completes | Shows completion screen (existing logic, keep) |
| Swipe task row (Create) | Drag handle → drag to reorder (existing logic, keep) |

### Screen Transitions
Keep existing `AnimatedContent` slide + fade transitions between Home → Create → Running.

### Quote Typing Animation (Home)
Keep existing `LaunchedEffect` + `delay(40ms)` per character. Expand `QUOTES` list to 20+ entries.

---

## Missing Backend Functions (add before UI work)

### 1. TaskDao.kt — Add TaskSummary query
```kotlin
data class TaskSummary(val taskCount: Int, val totalDuration: Int)

@Query("SELECT COUNT(*) as taskCount, COALESCE(SUM(durationSeconds),0) as totalDuration FROM tasks WHERE routineId = :routineId")
fun getTaskSummary(routineId: Long): Flow<TaskSummary>
```

### 2. RoutineRepository.kt — Expose it
```kotlin
fun getTaskSummary(routineId: Long): Flow<TaskSummary> = taskDao.getTaskSummary(routineId)
```

### 3. RoutineViewModel.kt — Add accessor
```kotlin
fun getTaskSummary(routineId: Long): Flow<TaskSummary> = repository.getTaskSummary(routineId)
```

### 4. Session time tracking (RunningRoutineScreen.kt)
```kotlin
var totalElapsedSeconds by remember { mutableIntStateOf(0) }
// In LaunchedEffect timer loop, when isPlaying && timeRemainingSeconds > 0:
totalElapsedSeconds += 1
```

---

## Assets & Icons

No external image assets. All visuals use:
- **Emojis** — task icons from `ICON_CATEGORIES` (already defined in `CreateRoutineScreen.kt`)
- **Material Icons** — `Icons.Rounded.*` and `Icons.Filled.*` (already a dependency)
- **Canvas drawing** — circular timer ring drawn programmatically

---

## Files in this Bundle

| File | Purpose |
|------|---------|
| `README.md` | This document — full implementation spec |
| `Phase 6 - UI Implementation Tasks.md` | Step-by-step ordered task list with code snippets |
| `Design System.dc.html` | Visual token + component reference (open in browser) |
| `Home Screen.dc.html` | Home screen mockup (open in browser) |
| `Empty State.dc.html` | Empty home screen mockup (open in browser) |
| `Create Routine.dc.html` | Create screen: default + duration picker open (open in browser) |
| `Running Routine.dc.html` | Running screen + completion screen (open in browser) |
| `Icon Picker.dc.html` | Icon picker: trigger + Fitness tab + Work tab (open in browser) |

---

## Design Rules (must follow for all new screens)

1. **Card elevation:** always `2.dp` shadow — never use tonal surface colors
2. **Tinted surfaces** (`BurntOrangeLight`) — content only (quote, icon circles, secondary buttons). Never interactive cards
3. **Section headers** — always: ExtraBold 10sp uppercase + 2dp NearBlack bottom rule + count badge right
4. **Bottom sheets** — always: 28dp top radius, white bg, 40px drag handle, 20dp horizontal padding
5. **No scaffold app bars** — every screen uses a custom nav bar: 36dp circular button + Nunito ExtraBold title
6. **Disable dynamic color** — `dynamicColor = false` in `RoutinesTheme`
7. **Metadata labels** — always: SubText color, ExtraBold, 10sp, uppercase, 1.5sp letter-spacing
