# Phase 6: UI Redesign Implementation

> **Design References:** All mockups live in the design project as `.dc.html` files.
> Open each file in the design tool to inspect exact layouts, spacing, and colors.
>
> | File | What it shows |
> |------|--------------|
> | `Home Screen.dc.html` | Filled home screen |
> | `Empty State.dc.html` | Empty home screen |
> | `Create Routine.dc.html` | Default state + duration picker bottom sheet |
> | `Running Routine.dc.html` | Ring timer screen + completion screen |
> | `Icon Picker.dc.html` | Trigger state + Fitness tab + Work tab |
> | `Design System.dc.html` | Full color/type/component reference |

---

## Design Tokens (reference for all tasks below)

```kotlin
// Colors
val BurntOrange      = Color(0xFFD4773E)   // primary accent
val BurntOrangeLight = Color(0xFFF7EDE5)   // tinted surface (quote card, icon circles)
val WarmWhite        = Color(0xFFF7F6F3)   // screen background
val CardWhite        = Color(0xFFFFFFFF)   // card surface
val NearBlack        = Color(0xFF0F0F14)   // primary text
val SubText          = Color(0xFFABABAB)   // secondary text / metadata
val BorderLight      = Color(0xFFEBEBEB)   // dividers
val NeutralPill      = Color(0xFFEEEDE9)   // collapsed duration pill bg

// Typography: Nunito via Google Fonts / downloadable font
// Weights used: 400 (regular/italic), 600, 700, 800, 900

// Key radius values
val RadiusCard   = 18.dp
val RadiusPill   = 28.dp
val RadiusSheet  = 28.dp   // top corners of bottom sheets
val RadiusInput  = 14.dp
val RadiusButton = 16.dp
val RadiusBadge  = 50.dp   // circle
```

---

## Step 1 — Backend (no UI, data layer only)

Do these before any UI work. All screens depend on them.

### 1.1 Add `TaskSummary` + query to `TaskDao.kt`

```kotlin
data class TaskSummary(val taskCount: Int, val totalDuration: Int)

@Query("""
    SELECT COUNT(*) as taskCount,
           COALESCE(SUM(durationSeconds), 0) as totalDuration
    FROM tasks WHERE routineId = :routineId
""")
fun getTaskSummary(routineId: Long): Flow<TaskSummary>
```

### 1.2 Expose in `RoutineRepository.kt`

```kotlin
fun getTaskSummary(routineId: Long): Flow<TaskSummary> =
    taskDao.getTaskSummary(routineId)
```

### 1.3 Add to `RoutineViewModel.kt`

```kotlin
fun getTaskSummary(routineId: Long): Flow<TaskSummary> =
    repository.getTaskSummary(routineId)
```

### 1.4 Add session elapsed time to `RunningRoutineScreen.kt` state

The completion screen shows total session time. Track it alongside the existing timer loop:

```kotlin
var totalElapsedSeconds by remember { mutableIntStateOf(0) }

// Inside the LaunchedEffect timer loop, when isPlaying && timeRemainingSeconds > 0:
totalElapsedSeconds += 1
```

Pass `totalElapsedSeconds` to the completion state UI.

---

## Step 2 — Design System / Theme

### 2.1 Add Nunito font

Add to `app/build.gradle.kts`:
```kotlin
implementation("androidx.compose.ui:ui-text-google-fonts:<version>")
```

Create `res/font/nunito_font_provider.xml` OR use downloadable fonts:
```kotlin
// In Type.kt
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val NunitoFont = GoogleFont("Nunito")
val NunitoFontFamily = FontFamily(
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.ExtraBold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Black),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Normal,
         style = FontStyle.Italic),
)
```

### 2.2 Update `Color.kt`

Replace the entire file with the design tokens above (BurntOrange, WarmWhite, etc.).

### 2.3 Update `Type.kt`

```kotlin
val Typography = Typography(
    displayLarge  = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Black,  fontSize = 48.sp, letterSpacing = (-2).sp),
    headlineLarge = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, letterSpacing = (-0.5).sp),
    headlineMedium= TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = (-0.5).sp),
    titleLarge    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,  fontSize = 17.sp, letterSpacing = (-0.3).sp),
    titleMedium   = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,  fontSize = 16.sp),
    bodyLarge     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp),
    bodyMedium    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 20.sp),
    labelLarge    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 2.sp),
    labelSmall    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,  fontSize = 10.sp, letterSpacing = 1.5.sp),
)
```

### 2.4 Update `Theme.kt`

- Set `dynamicColor = false`
- Build a `lightColorScheme` using:
  - `primary = BurntOrange`
  - `background = WarmWhite`
  - `surface = CardWhite`
  - `onPrimary = Color.White`
  - `onBackground = NearBlack`
  - `onSurface = NearBlack`
  - `surfaceVariant = BurntOrangeLight`

---

## Step 3 — Home Screen (`HomeScreen.kt`)

Work top-to-bottom. Wrap everything in a `Column` inside a `Box` (needed for the FAB overlay).

- [ ] **3.1** Remove `Scaffold` + `TopAppBar`. Use `Box(modifier = Modifier.fillMaxSize())` as root with a `Column` for content.
- [ ] **3.2** Custom header: `Text("Routines", style = MaterialTheme.typography.headlineLarge)` + `Spacer(2.dp)` + `Box(Modifier.width(48.dp).height(2.dp).background(NearBlack))`
- [ ] **3.3** Quote card: `Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = BurntOrangeLight), elevation = CardDefaults.cardElevation(0.dp))` — italic bodyMedium text, `minLines = 2`
- [ ] **3.4** Section header row: `"YOUR ROUTINES"` in `labelLarge` style (uppercase, tracked) + circular count badge `Box(22.dp, CircleShape, BurntOrange)` with `Text` count
- [ ] **3.5** `RoutineCard` — redesign:
  - `Card(shape = RoundedCornerShape(18.dp), elevation = 2.dp)`
  - Row: name (titleMedium, Bold) + sub-label (`"${taskCount} tasks · ${formatDuration(totalDuration)}"` in labelSmall, SubText)
  - Play button: `IconButton` in 38.dp circle, `BurntOrange` bg
  - Wire `getTaskSummary(routine.id).collectAsStateWithLifecycle()` into each card
- [ ] **3.6** Empty state: show `EmptyStateContent()` composable when `routines.isEmpty()` (see Step 7)
- [ ] **3.7** Replace `FloatingActionButton` with `ExtendedFloatingActionButton(text = { Text("Create Routine") }, icon = { Icon(Icons.Filled.Add) }, shape = RoundedCornerShape(28.dp), containerColor = BurntOrange)`

---

## Step 4 — Create Routine Screen (`CreateRoutineScreen.kt`)

- [ ] **4.1** Remove `Scaffold` + `TopAppBar`. Custom nav bar: circular back `IconButton` (36.dp, `WarmWhite` tint bg) + `Text("New Routine", style = titleLarge, fontWeight = ExtraBold)`
- [ ] **4.2** Routine name input: `Card(shape = RoundedCornerShape(14.dp), elevation = 2.dp)` wrapping a `Column` — `Text("ROUTINE NAME", labelSmall, SubText)` label + `BasicTextField` (no outline, Nunito titleMedium)
- [ ] **4.3** Tasks header: `"TASKS"` + count badge (same style as home screen)
- [ ] **4.4** `DraftTaskCard` — full redesign:
  - Single-row `Card(18.dp, elevation=2.dp)`: drag handle + icon circle (38.dp, `BurntOrangeLight`) + `BasicTextField` name + duration pill
  - Duration pill: `Surface(shape = RoundedCornerShape(20.dp), color = NeutralPill)` showing `formatDuration(task.durationSeconds)` + chevron icon — `Modifier.clickable { showDurationPicker = true }`
  - **Delete** button: 28.dp circle, `Color(0xFFFEE4E4)` bg, red X icon
  - **Remove entirely:** `DURATION_PRESETS`, `FilterChip` row, `showCustom`, `customMinsText`, `customSecsText` state and UI
- [ ] **4.5** Duration picker `ModalBottomSheet`:
  - Handle + `Text("Set Duration", titleLarge)` + task name badge
  - HH:MM:SS display: 3 groups of `Text(42.sp, ExtraBold)` with labels "hr / min / sec"
  - Entry state: `var durationBuffer by remember { mutableStateOf("000000") }` — 6-char string, digits fill from right
  - Numpad: `LazyVerticalGrid(Fixed(3))` for 1-9, empty cell, 0, backspace — each key is `Surface(RoundedCornerShape(14.dp), WarmWhite, height=52.dp)`
  - Backspace: removes last digit from buffer
  - On each digit press: shift buffer left, append digit → convert `HHMMSS` to seconds: `hh*3600 + mm*60 + ss`
  - "Confirm" button: full-width, `BurntOrange`, `RoundedCornerShape(16.dp)`
- [ ] **4.6** "Add Task" button: `OutlinedButton` with `BorderStroke(1.5.dp, BorderLight)` + dashed `PathEffect` — Row with `+` badge + `"Add Task"` text
- [ ] **4.7** Save button: fixed `Button` at bottom in `Box(Alignment.BottomCenter)` behind a fade gradient — `BurntOrange`, `RoundedCornerShape(16.dp)`, `"Save Routine"` label

---

## Step 5 — Icon Picker (`IconPickerSheetContent` in `CreateRoutineScreen.kt`)

- [ ] **5.1** Add `var selectedCategory by remember { mutableStateOf(ICON_CATEGORIES[0].first) }` state
- [ ] **5.2** Replace the category label + full grid loop with:
  - `LazyRow` of pill `FilterChip`s — selected = `BurntOrange` bg, unselected = `NeutralPill`
  - Filter icon list: `val icons = ICON_CATEGORIES.first { it.first == selectedCategory }.second`
- [ ] **5.3** `LazyVerticalGrid(columns = Fixed(5), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp))` — each item:
  - Selected: `Box(CircleShape, BurntOrange)` + green checkmark badge (`12.dp` circle, `Color(0xFF4CAF50)`, top-right corner)
  - Unselected: `Box(CircleShape, NeutralPill)`
  - Size: `52.dp`

---

## Step 6 — Running Routine Screen (`RunningRoutineScreen.kt`)

- [ ] **6.1** Remove `Scaffold` + `TopAppBar`. Custom nav bar: X close circle (36.dp, `NeutralPill` bg) + routine name (`titleLarge, ExtraBold`)
- [ ] **6.2** Progress indicators: `Row` of `tasks.size` pill-shaped `Box`es (24.dp × 6.dp):
  - Current = `BurntOrange`; completed = `BurntOrange.copy(alpha=0.4f)`; upcoming = `BorderLight`
  - Plus `Text("Task ${currentTaskIndex+1} of ${tasks.size}", labelSmall, SubText)`
- [ ] **6.3** Current task: centered `Box(52.dp, CircleShape, BurntOrangeLight)` with emoji + `Text(task.name, headlineMedium)`
- [ ] **6.4** Circular timer — replace `CircularProgressIndicator` with `Canvas(220.dp)`:
  ```kotlin
  val progress = if (task.durationSeconds == 0) 1f
                 else timeRemainingSeconds / task.durationSeconds.toFloat()
  // Track arc
  drawArc(color = Color(0xFFEEEDE9), startAngle = -90f, sweepAngle = 360f,
          useCenter = false, style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round))
  // Progress arc
  drawArc(color = BurntOrange, startAngle = -90f, sweepAngle = 360f * progress,
          useCenter = false, style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round))
  ```
  Overlay `Text(formatTime(timeRemainingSeconds), displayLarge.copy(48.sp))` + `"remaining"` label via `Box(contentAlignment = Alignment.Center)`
- [ ] **6.5** Time extenders: `Row` with 3 `SuggestionChip`s — `"+30s"`, `"+1m"`, `"+2m"` — `shape = RoundedCornerShape(20.dp)`, `containerColor = NeutralPill`; onClick adds 30/60/120 to `timeRemainingSeconds`
- [ ] **6.6** Controls: replace FAB + FilledIconButton with two `Button`s side-by-side:
  - Pause/Play: `Button(containerColor=BurntOrange, shape=RoundedCornerShape(16.dp), modifier=Modifier.height(56.dp))` — pause icon + `"Pause"` / play icon + `"Resume"`
  - Skip: `Button(containerColor=BurntOrangeLight, shape=RoundedCornerShape(16.dp), modifier=Modifier.height(56.dp))` — `"Skip"` + skip icon; `contentColor = BurntOrange`
- [ ] **6.7** Up next: replace `LazyColumn` with single `Card(14.dp, elevation=2.dp)` showing `tasks.getOrNull(currentTaskIndex + 1)` — icon circle + name + duration. Hide section when no next task.
- [ ] **6.8** Completion screen redesign (when `currentTask == null && tasks.isNotEmpty()`):
  - Check circle: `Box(88.dp, CircleShape, BurntOrangeLight)` + `Icon(Icons.Rounded.CheckCircle, BurntOrange, 40.dp)`
  - `Text("Routine\nComplete!", headlineLarge, textAlign=Center)`
  - Stats card: `Card(18.dp, elevation=2.dp)` → Row of two stat cells divided by `Divider(vertical)`:
    - Left: `Text(formatTime(totalElapsedSeconds), headlineMedium)` + `"TOTAL TIME"` label
    - Right: `Text("${tasks.size}", headlineMedium)` + `"TASKS DONE"` label
  - Quote card: `Card(14.dp, BurntOrangeLight)` — random quote from `QUOTES` list, italic bodyMedium
  - Done button: `Button(BurntOrange, RoundedCornerShape(16.dp), fillMaxWidth)`

---

## Step 7 — Empty State composable (add to `HomeScreen.kt`)

- [ ] **7.1** Create `@Composable fun EmptyStateContent()`:
- [ ] **7.2** Ghost card stack via `Box(height=136.dp)`:
  - Back card: `Card(18.dp, elevation=0.dp, alpha=0.05f, fillMaxWidth padding 20.dp)`
  - Mid card: `Card(18.dp, elevation=0.dp, alpha=0.08f, fillMaxWidth padding 10.dp, offset 12.dp)`
  - Front card: `Card(18.dp, elevation=2.dp)` with skeleton: `Box(38.dp, CircleShape, NeutralPill)` + two `Box` skeleton lines (BurntOrangeLight bg, heights 12.dp / 9.dp, widths 55% / 30%)
- [ ] **7.3** `Text("No routines yet", headlineMedium, ExtraBold, textAlign=Center)`
- [ ] **7.4** `Text("Build your first routine and start making your days count.", bodyMedium, SubText, textAlign=Center)`
- [ ] **7.5** Hint: `Text("tap the button below to begin", labelSmall, SubText)` + `Icon(Icons.Rounded.KeyboardArrowDown, SubText)`

---

## Step 8 — Housekeeping

- [ ] **8.1** Remove all remaining references to the old purple palette (`Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40`) from `Color.kt`
- [ ] **8.2** Remove `DURATION_PRESETS` constant from `CreateRoutineScreen.kt` (no longer used)
- [ ] **8.3** `formatTime()` in `RunningRoutineScreen.kt` — extend to support hours: `String.format("%02d:%02d:%02d", h, m, s)` when hours > 0
- [ ] **8.4** Update `QUOTES` list in `HomeScreen.kt` — expand to 20+ quotes (currently only 5)
- [ ] **8.5** Verify all `AnimatedContent` screen transitions still work correctly after scaffold removal

---

## Completion Criteria for Phase 6

All screens match the design references. Verified when:
- [ ] Home screen shows routine cards with task count + duration metadata
- [ ] Empty state shows ghost cards + hint + extended FAB
- [ ] Create Routine has compact task rows + HH:MM:SS numpad bottom sheet
- [ ] Icon picker uses horizontal category tabs + 5-per-row grid
- [ ] Running screen shows circular canvas timer + rectangular controls + single up-next card
- [ ] Completion screen shows stats card + quote tint card
- [ ] Nunito font loads correctly on all text
- [ ] `#D4773E` burnt orange used consistently (no purple remnants)
- [ ] No dynamic color (wallpaper tinting disabled)
