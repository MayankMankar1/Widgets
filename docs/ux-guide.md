# 100Widgets UX guide

The single reference for how the app looks, moves and behaves. Follow it for every new screen,
and update it in the same change when a decision changes. Structure follows
[MockFlow's UX documentation guide](https://mockflow.com/blog/UX-documentation-for-mobile-app-design);
motion references come from [60fps.design](https://60fps.design) (see the end).

Last updated: 2026-11-01.

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
├── Wallpapers ── grid ─► Full-screen preview (in place)
└── Settings ── clock format, refresh interval, battery help, about
Separate activity: design chooser (opened by the launcher)
```

Pages opened from a tab keep that tab highlighted in the bottom bar.

## 4. Style guide

### Colour (dark only; widgets and wallpapers are the brightest things on screen)

| Token | Hex | Use |
|---|---|---|
| Background / surface | `#0E0F12` | Screen background |
| Surface container | `#17181C` | Cards, sheets |
| Surface high | `#212228` | Chips, step numbers, raised fills |
| Accent (primary) | `#FF9F45` | Primary buttons, selected states, success marks |
| On accent | `#2B1600` | Text and icons on the accent |
| Text | `#F2F1EC` | Primary text |
| Text muted | `#A6A7AD` | Secondary text, metadata |
| Outline | `#3A3B42` | Borders, inactive dots, shimmer highlight |

System bars are transparent with **light** icons in every phone mode.

### Type
Material 3 type scale with the system font. Screen titles `headlineMedium`, card titles
`titleMedium`, metadata `bodySmall` in muted text. Theme fonts belong to widgets, not to the app.

### Shape and spacing
- Corners: 32 dp hero images and scene cards, 28 dp design cards, 24 dp tiles and sheets' inner images, 12 dp section cards.
- Spacing on a 4 dp grid: 16 dp screen padding, 12–16 dp between cards, 8 dp between chips.
- Touch targets at least 48 dp; primary buttons 56 dp tall and full width.

### Components (in `app/ui/components` and `app/ui/motion`)
| Component | Rule |
|---|---|
| `DesignBackdrop` | Every design is shown on its theme's wallpaper, never on a flat card |
| `WidgetPreview` | The real widget render; shimmer while it draws, then fades and grows in |
| Scene card | Pager card, 400 dp tall, wallpaper with a dark gradient for text |
| Bottom sheet | For multi-step actions (add widget); never an alert dialog |
| Filter chips | Selected chip shows a check and grows to fit it |

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
- **Tabs:** fade-through: out in 150 ms, then in with a 0.96 → 1 zoom.
- **Card → detail:** the wallpaper backdrop and the title morph (shared element); the rest fades.
- **Thumbnail → wallpaper preview:** the image grows to full screen (shared element).
- **Press:** cards shrink to 0.96 and spring back (`Modifier.pressable`).
- **Lists:** items fade and rise 24 dp, staggered, only the first time (`Modifier.enterOnce`); filter changes animate placement (`animateItem`).
- **Carousel:** off-centre cards scale to 0.88 and dim to 55 %; wallpapers drift at 25 % speed (parallax).
- **Loading:** shimmer, never a spinner, for content that is being drawn.
- **Success:** a check mark springs in (`bouncy`), plus a confirm haptic.

**Reduced motion:** when the phone's "Remove animations" setting is on (`LocalReducedMotion`),
skip morphs, parallax, staggering, shimmer sweeps and press scaling. State changes still happen
instantly.

## 6. Interaction and feedback

| Moment | Feedback |
|---|---|
| Switch tab, filter chip, setting, carousel settles | `SegmentTick` haptic |
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

1. Run it on the emulator: every screen, both entry and back.
2. Turn on Developer options → "Remove animations" and repeat: nothing breaks, nothing waits.
3. `./gradlew test lint` with no new errors.
4. Before a release, check on a real phone (Nothing, Samsung, Pixel, Xiaomi launchers).

## References

Motion and pattern references studied on [60fps.design](https://60fps.design): Pool widget-size swipe sheet, Pool grid-to-screenshot morph, Airbnb media morph preview, CRED stacked bottom sheets, Apple wallpapers swipe, Blinkit theme-change pulse. Structure from [Refero](https://refero.design) ("Personalizing & Customizing" flows) and [Mobbin](https://mobbin.com).
