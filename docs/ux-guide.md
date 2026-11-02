# 100Widgets UX guide

The single reference for how the app looks, moves and behaves. Follow it for every new screen,
and update it in the same change when a decision changes. Structure follows
[MockFlow's UX documentation guide](https://mockflow.com/blog/UX-documentation-for-mobile-app-design);
motion references come from [60fps.design](https://60fps.design) (see the end).

Last updated: 2026-11-02.

---

## 1. Who it's for

| Persona | Goal | Pain today | What the app must do |
|---|---|---|---|
| **The styler** (18–30, changes setup often) | A home screen that looks designed | Widget apps sell pieces that don't match | Show whole scenes; make a theme feel like one product |
| **The tidy-minimalist** | One calm, readable screen | Too many settings, cluttered widgets | Few choices per screen, clear defaults, no ads |
| **The first-timer** | "How do I even add a widget?" | Launchers hide the widget picker | Explain the add flow in two steps, confirm success |

## 2. Core flows

1. **Apply a scene:** Home → swipe the scenes carousel → tap a scene → that theme's designs (Widgets tab, filtered) → design detail → Add.
2. **Add one widget:** Widgets → (filter) → tap a card (backdrop morphs into the detail page) → **Add to home screen** → sheet explains 2 steps → **Add widget** → launcher's confirm dialog → success check in the sheet.
   - Error path: the launcher can't pin → the sheet shows manual steps instead of failing silently.
3. **Set a wallpaper:** Wallpapers → tap a thumbnail (it grows into a full-screen preview with a sample clock) → Home / Lock / Both → in-place progress → success check, preview closes by itself.
   - Error path: "Couldn't set the wallpaper" with the buttons still there to retry.
4. **Change a placed widget:** long-press it on the home screen → widget settings → design chooser.

## 3. Screen map

```
Bottom tabs
├── Home ── scenes carousel ─► Widgets (filtered by theme)
│        └─ your widgets ─────► Design detail
├── Widgets ── theme chips, design cards ─► Design detail ─► Add sheet
├── Wallpapers ── 2-column grid ─► Full-screen preview (in place)
└── Settings ── Appearance (theme) · Widgets (clock format, updates) · Help · About
Separate activity: design chooser (opened by the launcher)
```

Pages opened from a tab keep that tab highlighted in the bottom bar.

## 4. Style guide

Direction: calm and precise (Linear, Nothing OS, Apple, Raycast). Flat surfaces separated by
tone, not by shadows or borders. No gradients (except the dark scrim that keeps text readable on
wallpapers), no glass, no neon. Widgets and wallpapers are the most colourful things on screen.
All tokens live in `app/ui/theme/Theme.kt`.

### Colour

The app follows **Settings → Appearance → Theme** (System, Light, Dark; default System, stored by
`SettingsRepository.appTheme`). Widgets never follow it: they keep their design theme's colours.
Switching cross-fades every colour over `LONG` with `Emphasized`; the system bar icons flip with it.

| Token (`colorScheme`) | Dark | Light | Use |
|---|---|---|---|
| `background` / `surface` | `#0B0B0D` | `#F7F7F5` | Screen background |
| `surfaceContainer` | `#17171A` | `#FFFFFF` | Cards, rows, sheets, bottom bar |
| `surfaceContainerHigh` | `#202024` | `#EEEEEB` | Segmented track, icon tiles, raised fills |
| `primary` (accent, violet) | `#9B8AFB` | `#6D5BF0` | Primary buttons, selected states, success marks |
| `onPrimary` | `#160E3D` | `#FFFFFF` | Text and icons on the accent |
| `primaryContainer` | `#2A2350` | `#EAE6FF` | Selected chip, selected tab pill |
| `onPrimaryContainer` | `#D9D2FF` | `#2B1F7A` | Icon and label on the selected tab pill |
| `onSurface` | `#F5F5F5` | `#111113` | Primary text |
| `onSurfaceVariant` | `#929299` | `#6B6B73` | Secondary text, metadata, unselected icons |
| `outline` | `#2E2E33` | `#DCDCD8` | Chip borders, inactive dots, shimmer highlight |

Use colours only through `MaterialTheme.colorScheme`; never a hex value in a screen. Text on a
wallpaper is always white on the dark scrim, in both modes.

Launch never flashes the wrong palette: `values/` and `values-night/` set the window background
to the light and dark `background`.

### Type

Inter (Regular, Medium, SemiBold; Latin-only cuts in `app/src/main/res/font/`), via `AppTypography`.
Large sizes get slightly negative tracking.

| Style | Size / line | Weight | Use |
|---|---|---|---|
| `displaySmall` | 34 / 40 | SemiBold | Rare, big numbers |
| `headlineLarge` | 30 / 36 | SemiBold | Screen titles (`ScreenTitle`) |
| `headlineSmall` | 22 / 28 | SemiBold | Scene card names, detail titles |
| `titleMedium` | 16 / 22 | SemiBold | Card titles |
| `titleSmall` | 14 / 20 | SemiBold | Row and setting titles |
| `bodyMedium` | 14 / 20 | Regular | Body text |
| `bodySmall` | 13 / 18 | Regular | Metadata, setting descriptions (muted) |
| `labelLarge` / `labelMedium` / `labelSmall` | 14 / 12 / 11 | Medium | Buttons, tab labels, group labels |

Theme fonts belong to widgets, not to the app.

### Spacing, radius and icon size

| `Spacing` | dp | | `Radius` | dp | | `IconSize` | dp |
|---|---|---|---|---|---|---|---|
| `xs` | 4 | | `hero` | 28 | | `sm` | 18 |
| `sm` | 8 | | `card` | 24 | | `md` | 22 |
| `md` | 12 | | `tile` | 20 | | `lg` | 24 |
| `lg` | 16 | | `row` | 16 | | | |
| `xl` | 20 | | `chip` | 12 | | | |
| `xxl` | 24 | | | | | | |
| `section` | 32 | | | | | | |
| `screen` | 20 | | | | | | |

- `hero`: scene cards and full-bleed images. `card`: design cards, section cards, settings groups. `tile`: wallpaper tiles. `row`: list rows. `chip`: chips, segmented controls, small thumbnails.
- 16 dp horizontal screen padding for lists, 20 dp for titles; 24 dp between settings groups.
- Touch targets at least 48 dp; primary buttons 56 dp tall and full width.

### Components (in `app/ui/components` and `app/ui/motion`)
| Component | Rule |
|---|---|
| `ScreenTitle` | Every tab starts with one; optional muted subtitle |
| `SectionCard` | Flat `surfaceContainer` card, `Radius.card`, no elevation |
| `DesignBackdrop` | Every design is shown on its theme's wallpaper, never on a flat card |
| `WidgetPreview` | The real widget render; shimmer while it draws, then fades and grows in |
| Scene card | Pager card, 400 dp tall, the theme's backdrop (`ThemeInfo.backdrop`) with a dark scrim for text |
| `BundledImage` | Every bundled bitmap in a list or card: decoded once off the main thread, shared, fades in. Never `painterResource` for photos in lists |
| Placed widget row | Wallpaper thumbnail, title, theme · size, chevron |
| `SettingsGroup` / `SettingsItem` | Uppercase muted label above a card; each item has a 36 dp icon tile, title, optional description and control |
| `SegmentedControl` | For 2–4 mutually exclusive options (theme, clock format, interval); a pill slides to the choice. Prefer it over radio lists |
| Bottom sheet | For multi-step actions (add widget); never an alert dialog |
| Filter chips | `Radius.chip`; selected chip uses `primaryContainer`, shows a check and grows to fit it |
| `TabBar` | Floating pill (`surfaceContainer`, 1 dp `outlineVariant` border) inset 20 dp from the sides. The selected tab is a `primaryContainer` pill with icon and label; the others show only their icon |

## 5. Motion

All values live in `app/ui/motion/Motion.kt`. Never hard-code a duration or spring elsewhere.

| Token | Value | Use |
|---|---|---|
| `SHORT` | 150 ms | Chips, icons, exits |
| `MEDIUM` | 300 ms | Screen fades, sheets, content swaps |
| `LONG` | 450 ms | Hero morphs, list entrances |
| `STAGGER` | 45 ms per item (max 8 items) | List items entering one after another |
| `Emphasized` | cubic (0.2, 0, 0, 1) | Morphs and moves |
| `EmphasizedDecelerate` | cubic (0.05, 0.7, 0.1, 1) | Things arriving |
| `EmphasizedAccelerate` | cubic (0.3, 0, 0.8, 0.15) | Things leaving |
| `gentle()` | spring, damping 0.85, medium-low stiffness | Size and position changes |
| `bouncy()` | spring, medium bounce | Press feedback, toggles, success marks |

**Patterns**
- **Tabs:** the old screen fades out (`SHORT`); the new one fades in and rises 4 % of its height (about 30 dp) over `LONG` with `EmphasizedDecelerate`. Opening or leaving a detail page only fades.
- **Tab bar:** the selected pill widens to show its label and the old one shrinks back (`gentle`); colours cross-fade over `MEDIUM`.
- **Card → detail:** the wallpaper backdrop and the title morph (shared element); the rest fades.
- **Thumbnail → wallpaper preview:** the image grows to full screen (shared element).
- **Press:** cards shrink to 0.96 and spring back (`Modifier.pressable`).
- **Lists:** items fade and rise 24 dp, staggered, only the first time (`Modifier.enterOnce`); filter changes animate placement (`animateItem`).
- **Carousel:** snaps with the pager's spring; off-centre cards scale to 0.92 and dim to 60 %. No parallax.
- **Segmented control:** the pill slides with `gentle`; label colours cross-fade.
- **Theme switch:** all colours cross-fade over `LONG` (`Emphasized`).
- **Loading:** shimmer, never a spinner, for content that is being drawn.
- **Success:** a check mark springs in (`bouncy`), plus a confirm haptic.

**Reduced motion:** when the phone's "Remove animations" setting is on (`LocalReducedMotion`),
skip morphs, tab slides, staggering, shimmer sweeps, icon scaling and press scaling. State changes still happen
instantly.

## 6. Interaction and feedback

| Moment | Feedback |
|---|---|
| Filter chip, setting, carousel settles | `SegmentTick` haptic |
| Switch tab | Nothing: the bar's own animation is enough, and a tick on every tab change felt noisy |
| Favourite on / off | `ToggleOn` / `ToggleOff` haptic, heart pops |
| Widget add requested, wallpaper set | `Confirm` haptic + check mark |
| Wallpaper failed | `Reject` haptic + message, buttons stay for retry |
| Anything that takes time | Visible progress in place (shimmer or spinner), never a silent wait |

- Back always works: system back and the predictive back gesture close previews and pop screens.
- Never a toast for something the user is looking at; show the result where they acted.

## 7. Accessibility

- Every icon-only button has a content description that says what a tap does (for example "Save to favourites" / "Remove from favourites").
- Clickable cards announce an action label ("Open").
- Text contrast at least 4.5 : 1 on its background (text on wallpapers sits on a dark gradient).
- Touch targets at least 48 dp; radio rows are full-width.
- Motion respects "Remove animations" (section 5).
- All user-facing text lives in `strings.xml`.

## 8. Checking a change

1. Run it on a phone or the emulator: every screen, both entry and back, in **Light and Dark**.
2. Turn on Developer options → "Remove animations" and repeat: nothing breaks, nothing waits.
3. `./gradlew test lint` with no new errors.
4. Before a release, check on a real phone (Nothing, Samsung, Pixel, Xiaomi launchers).

## References

Motion and pattern references studied on [60fps.design](https://60fps.design): Pool widget-size swipe sheet, Pool grid-to-screenshot morph, Airbnb media morph preview, CRED stacked bottom sheets, Apple wallpapers swipe, Blinkit theme-change pulse. Structure from [Refero](https://refero.design) ("Personalizing & Customizing" flows) and [Mobbin](https://mobbin.com).
