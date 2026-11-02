# CLAUDE.md

Guidance for Claude Code working in this repository. `README.md` is the full blueprint (product, architecture, database, widget inventory). Read the relevant README section before changing an area, and update the README when a decision or structure changes.

## Project in one paragraph

100Widgets is a native Android app (Kotlin, Jetpack Compose for the app, Jetpack Glance for widgets) that offers themed home-screen widgets and wallpapers. The visual spec for every widget is the HTML/CSS design canvas linked in README §3; widgets are rebuilt natively from it, never embedded as HTML.

**Status:** early development. 22 designs across 6 themes build, pass `test` and `lint`, and run on a real phone (README §2, "What exists in code today"). Next: the designs that need new storage or settings, and launcher checks on Pixel, Samsung and Xiaomi.

## Working agreement

- **Discuss the plan before any commit.** Propose the approach, wait for the user's OK, and ask before every `git commit` or push.
- v1 scope (README §2):
  - 6 themes: Horizon, Noir, Paper, Signal, Bauhaus, Comic. **Paper and Signal ship only designs that need no special permission.**
  - **No weather.** No weather API, key or location-for-weather.
  - **Free, no ads.** Pro and ads may come later.
- Don't build designs marked *Later*, or Paper/Signal permission designs, unless the user asks.
- When something on the canvas can't work on Android, say so and propose an *Adapt* option (README §5 has the pattern). Don't silently drop or fake it.

## Commands

Run from the repo root. On Windows use `gradlew.bat` (or `./gradlew` from Git Bash). Without a separate JDK, set `JAVA_HOME` to Android Studio's `jbr` folder first (README §17, "First build").

```bash
./gradlew assembleDebug
./gradlew installDebug
./gradlew test
./gradlew connectedDebugAndroidTest
./gradlew lint
./gradlew bundleRelease
```

Before saying a change is done, run `./gradlew test lint` for the touched modules. If you can't build (no SDK), say so plainly instead of claiming it works.

## Modules

```
app ──► widget:data ──► widget:engine ──► widget:designs ──► core:model
                                     └──► core:data ──► core:database
```

- `:app`: activities, navigation, screens (`ui/`), `ConfigureActivity`, `WallpaperApplier`
- `:widget:designs`: `WidgetDesign`, `WidgetRegistry`, one folder per theme, TextClock/AnalogClock XML
- `:widget:engine`: `HundredWidget`, 6 size receivers, `WidgetPinner`, `WidgetUpdater`, widget info XML
- `:widget:data`: collectors, `RefreshWorker`, `RefreshScheduler`, `WidgetRefreshCoordinator`
- `:core:data`, `:core:database`, `:core:model`: repositories, Room, plain models

Versions live only in `gradle/libs.versions.toml`.

## Architecture rules

### Widgets
- **One receiver per size, never per design.**
  - There are 6 receivers: 1×1, 1×2, 2×1, 2×2, 4×1, 4×2. All use the single `HundredWidget`.
  - A new widget is a new `WidgetDesign` entry in its theme's `*Designs.kt` plus its composable. Never a new manifest receiver.
- **Design IDs are permanent** (`theme.snake_name`, e.g. `horizon.clock_quote`). They're stored on users' phones. Never rename or reuse one; hide it with Remote Config `disabled_design_ids` (planned) or deprecate it.
- **Widget code uses Glance only.**
  - Use `androidx.glance.*` composables.
  - From Compose, only `androidx.compose.runtime.*` (state, `collectAsState`), `androidx.compose.ui.graphics.Color` and `androidx.compose.ui.unit` (dp/sp) are allowed.
  - Never `androidx.compose.foundation`, `material3` or `ui.Modifier`/layout in widget modules: they compile but don't work in RemoteViews.
- **Clocks:**
  - Use `GlanceTextClock` / `GlanceAnalogClock`. The launcher ticks them.
  - Theme fonts come from `WidgetFont` (`GlanceTextClock` for live time, `GlanceStyledText` for static text); Glance's own `Text` only has system fonts. A new font means a TTF in `res/font/` plus its three layouts and a `WidgetFont` entry.
  - Never put an `AndroidRemoteViews` straight into a Column or Row: it breaks the parent's layout. Use the boxed helpers above.
  - **Never** schedule per-minute or per-second updates. Never use exact alarms.
  - Never add a foreground service for live faces unless the user decides to (README §20).
- **Widgets never touch the network.** They read Room + the `DeviceSnapshot` only. Images are bundled or pre-downloaded by the app.
- **Bitmaps** are sized to the widget's largest cell size. The RemoteViews bitmap memory cap is roughly screen w × h × 4 × 1.5 bytes per update.
- **User photos:** copy the picked image into `filesDir/widget_photos/` and render it as a bitmap. The launcher can't read our content URIs.
- **Rendering must not crash.** `HundredWidget.errorUiLayout` catches failures. Designs fall back to sample data only when `isPreview` is true; otherwise they show a placeholder like `--%`.
- **Updates go through `WidgetUpdater.refresh(source)`.** It redraws only instances whose design `needs` that source. Collectors return false (and skip the write) when the value hasn't changed.
- **Android 12+ only features** need a fallback for API 26–30:
  - Glance `cornerRadius` (use rounded background drawables instead, as the starter designs do)
  - responsive sizes
  - check boxes
  - the second hand (`layout-v31`)
  - `targetCellWidth`
- **PendingIntents** are `FLAG_IMMUTABLE`, except the `requestPinAppWidget` success callback, which must be `FLAG_MUTABLE`.
- **Receivers:** `GlanceAppWidgetReceiver` already calls `goAsync()` in `onDeleted`/`onUpdate`. Don't call it again there; use the receiver's coroutine scope.

### Data and storage
- **Room:**
  - `exportSchema = true`.
  - Until the first Play release, version 1 may be edited in place.
  - After release, every schema change = version bump + `Migration` + migration test.
  - Never `fallbackToDestructiveMigration` (users would lose their placed widgets).
- Per-widget UI-only state (quote index, torch flag…) lives in Glance state, not Room.
- Settings go through `SettingsRepository`. Device data goes through `DeviceSnapshotRepository`.
- "Is this unlocked?" is answered only by `EntitlementRepository` (returns `FREE` in v1). Never hardcode tier checks.
- Glance classes, receivers and workers get dependencies through Hilt `@EntryPoint`s (`WidgetEngineEntryPoint`, `WidgetDataEntryPoint`), not constructor injection.

### Permissions (README §14)
- Request just in time, when the user adds a design that needs it, after a rationale screen.
- v1 needs at most:
  - `READ_CALENDAR`
  - `ACTIVITY_RECOGNITION`
  - DND access
  - `SET_WALLPAPER` (normal)
  - `INTERNET` (once the online catalogue lands)
- **Never add:**
  - `ACCESS_BACKGROUND_LOCATION`
  - `QUERY_ALL_PACKAGES` (use `<queries>` for launcher intents)
  - `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`
  - an accessibility service
  - `CAMERA` (the torch uses `CameraManager.setTorchMode`)
- A new permission means updating README §14 and flagging that the Play Data safety form must change.

### Monetization
- No ads anywhere in v1.
- If ads are added later, they go in app screens only, **never inside a widget**.

## Conventions

- **Kotlin:** official style, coroutines + Flow, `StateFlow` UI state per screen, Hilt (`@HiltViewModel`, `hiltViewModel()`).
- **Naming:** `*Repository`, `*Dao`, `*Entity`, `*Collector`, `*Worker`, `*Receiver`, `*ViewModel`, `*Screen`.
- **Design files:** `widget/designs/.../<theme>/<Theme><Name>.kt` (e.g. `horizon/HorizonClockQuote.kt`), registered in `<theme>/<Theme>Designs.kt`.
- **Resource prefixes:**
  - `wd_` in `:widget:designs`, `we_` in `:widget:engine`
  - theme in the name (`wd_bg_horizon_glass`, `wd_bauhaus_disc_hour`)
  - app wallpapers are `wp_<name>` (theme ones `wp_<theme>`), with a `wpt_<name>` thumbnail
- **Strings:** all user-facing text in `strings.xml` (localisation comes later). No hardcoded UI strings.
- **Comments:** short KDoc on classes explaining *why*; avoid restating code.
- **App UX and motion:** follow `docs/ux-guide.md`. Durations, easings and springs come only from `app/ui/motion/Motion.kt`; use `pressable`, `enterOnce`, `shimmer` and `sharedHero` rather than one-off animations, and respect `LocalReducedMotion`.
- **App look:** colours only via `MaterialTheme.colorScheme`, and spacing, corners, icon sizes and type from `Spacing`, `Radius`, `IconSize` and `AppTypography` in `app/ui/theme/Theme.kt`. Every screen must work in light and dark (Settings → Appearance). Widgets never follow the app theme.
- **Dependencies:** don't add one without saying why.

## Adding a widget design (checklist)

1. Check the design's row in README §5 (status, size, notes). Add a row if it's new, and mark it **built** when done.
2. Add assets:
   - vector icons and backgrounds → `drawable/`
   - photo tiles and pre-rendered art → `drawable-nodpi/` (WebP, sized to the cell)
   - fonts → `font/` plus a TextClock layout
3. Write the Glance composable in the theme folder.
4. Register a `WidgetDesign` with:
   - an ID
   - theme, category and size
   - `needs`, `permissions` and `tier`
   - `addedIn` = the next versionCode
5. Run `WidgetRegistryTest`. Check the in-app preview, then a real launcher at the smallest and largest supported size.

## Adding wallpapers

- **Today:** bundled wallpapers live in `app/src/main/res/drawable-nodpi/` as `wp_<name>.webp` plus a 400×889 `wpt_<name>.webp` thumbnail, made by `tools/wallpapers/bundle.py` (renders come from `tools/wallpapers/build.py`, see its README). They're listed by collection in `app/ui/wallpapers/WallpaperCatalog.kt`; theme wallpapers are also mapped in `app/ui/ThemeInfo.kt`.
- Grids and rows show only thumbnails; load the full image only in the preview or when setting it.
- Keep the bundled set under about 10 MB. Beyond that, wallpapers go online.
- **Planned:** online wallpapers go through the R2 catalogue and pipeline (README §13), with no app change needed per wallpaper.

## Secrets and files never to commit

- keystores
- signing passwords
- R2 credentials
- `local.properties`
- any `.env`

Keystore and signing values come from `local.properties` or environment variables.

## Android gotchas to remember

- Apps can't toggle Wi-Fi (Android 10+), Bluetooth (13+), location, mobile data or power. Open the system panel or settings screen instead.
- Apps can't place widgets at exact positions. `requestPinAppWidget` asks the user, and some launchers don't support it. Show manual instructions then.
- WorkManager's minimum periodic interval is 15 minutes. Phone-brand battery savers (Xiaomi, Vivo, Oppo, Realme, Samsung) can delay or kill work.
- Reading location from a background refresh needs background-location permission. Only read location in the foreground (on tap).
- There is no public API for earbud/watch battery, the mic toggle, or starting the system screen recorder.
