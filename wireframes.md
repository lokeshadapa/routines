# Routines App Wireframes

This document contains the ASCII wireframes for the core screens of the Routines app.

## Screen 1: Home Screen
Features the app header, a dynamic typing-effect quote, a list of existing routines, and a FAB to create a new routine.

```text
+---------------------------------------+
| 10:00                       LTE [100%]|
|---------------------------------------|
|                                       |
|  ROUTINES                             |
|                                       |
|  "The secret of your future is        |
|   hidden in your daily routin|"       |
|                                       |
|---------------------------------------|
|                                       |
|   [ Morning Workout               > ] |
|                                       |
|   [ Read 30 Mins                  > ] |
|                                       |
|   [ Evening Wind Down             > ] |
|                                       |
|                                       |
|                                       |
|                                       |
|                                       |
|                                       |
|                                       |
|                                  (+)  |
+---------------------------------------+
```

## Screen 2: Create Routine Screen
Allows the user to set a routine name, add tasks with icons and durations, and reorder them. 

```text
+---------------------------------------+
| 10:00                       LTE [100%]|
|---------------------------------------|
| <-  CREATE ROUTINE             [SAVE] |
|---------------------------------------|
|                                       |
|  Routine Name                         |
|  [ e.g., Morning Workout            ] |
|                                       |
|---------------------------------------|
|  TASKS                                |
|                                       |
|  =  (💪) Warmup                        |
|     Duration: [ 05:00 ]               |
|     [ 5m ] [ 10m ] [ 15m ] [ 30m ]    |
|                                       |
|  =  (🏃) Running                       |
|     Duration: [ 30:00 ]               |
|     [ 5m ] [ 10m ] [ 15m ] [ 30m ]    |
|                                       |
|---------------------------------------|
|                                       |
|            + ADD NEW TASK             |
|                                       |
|                                       |
+---------------------------------------+
```

## Screen 3: Running Routine Screen
The active screen when a user starts a routine. It displays the current task, a large countdown timer, playback controls (Pause/Skip), and the upcoming tasks.

```text
+---------------------------------------+
| 10:00                       LTE [100%]|
|---------------------------------------|
| [X] Close       Morning Workout       |
|---------------------------------------|
|                                       |
|             (🏃) Running               |
|                                       |
|           /---------\                 |
|          /           \                |
|         |    25:14    |               |
|          \           /                |
|           \---------/                 |
|            [ +1 MIN ]                 |
|                                       |
|       [ II PAUSE ]   [ >| SKIP ]      |
|                                       |
|---------------------------------------|
|  UP NEXT                              |
|                                       |
|  (🧘) Stretching          10:00       |
|                                       |
|  (🚿) Shower              15:00       |
|                                       |
|                                       |
|                                       |
+---------------------------------------+
```
