<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Architecture — Word of the Day (Android)

## Overview

Planned **single-activity** Android app using **Jetpack Compose** for UI and **Kotlin** throughout. Business rules and async work sit outside composables; UI observes immutable **UI state**.

## Layers (target)

1. **UI (Compose)**  
   Screens, theme, strings, navigation. Calls ViewModel intents / events only.

2. **Presentation (ViewModel)**  
   Exposes `UiState` and handles user actions; uses coroutines (`viewModelScope`).

3. **Domain (optional thin use cases)**  
   If logic grows, isolate “what is today’s word?” policy here.

4. **Data**  
   - **Repository** merges remote + local sources.  
   - **Remote**: API client (e.g. Retrofit/Ktor) when not bundled-only.  
   - **Local**: DataStore / Room for preferences or cache (when added).

## Module layout (suggested)

- `:app` — application module, navigation, DI entry (if used).  
- Future `:core:model`, `:core:data` if the app outgrows a single module.

## Form factors

- **Adaptive UI** driven by window size / width breakpoints (Material 3 adaptive patterns).  
- Shared ViewModel for a screen where possible; avoid duplicating “today” logic per device class.

## Diagram (conceptual)

```mermaid
flowchart LR
  subgraph ui [Compose UI]
    Screens[Screens]
  end
  subgraph pres [Presentation]
    VM[ViewModel]
  end
  subgraph data [Data]
    Repo[Repository]
    Remote[Remote API]
    Local[Local cache]
  end
  Screens --> VM
  VM --> Repo
  Repo --> Remote
  Repo --> Local
```

`[2026-03-29]` Initial architecture notes.
