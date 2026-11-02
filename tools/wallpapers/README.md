# Wallpaper renders

Our own 3D wallpapers, rendered in Blender, so every image is ours to ship (no stock or AI licences).

| File | Runs in | Does |
|---|---|---|
| `render.py` | Blender 5.x | Builds and renders one wallpaper: a collection name plus a variant number |
| `build.py` | Python 3 (numpy, Pillow) | Renders a batch, adds fine film grain, writes WebP and a contact sheet |
| `bundle.py` | Python 3 (Pillow) | Copies chosen renders and extra images into `app/src/main/res/drawable-nodpi` as `wp_*` (q85) plus `wpt_*` thumbnails |

```bash
py -I tools/wallpapers/build.py glass clay clay_night --count 6 --preview   # quick layout check, ~6 s each
py -I tools/wallpapers/build.py glass clay clay_night --count 6             # full 1440x3200, ~45 s each on an RTX 3050
```

Output goes to `tools/wallpapers/out/` (git-ignored). Set `BLENDER` if Blender isn't in
`C:\Program Files\Blender Foundation\Blender 5.2\`. Cycles uses CUDA first, because the OptiX
kernel failed to load with the current NVIDIA driver.

## Collections

| Collection | Look | Variants |
|---|---|---|
| `glass` | Clear and tinted glass floating in the dark, edges lit by off-frame light strips | 6 tints (amber, ice, rose, jade, violet, mono) × hero shapes (torus, block, capsule, sphere, ring) |
| `clay` | Matte shapes on a light floor from above, long soft shadows, calm middle for widgets | 6 palettes (terracotta, sage, ocean, blush, lemon, mono), seeded layouts |
| `clay_night` | The same shapes on a dark floor under a low coloured sun | Same palettes, night floors |

A variant number always renders the same picture (the layout RNG is seeded from it), so a
wallpaper can be re-rendered at another size later. Variants past 6 reuse the palettes with new
layouts.

## Rules

- 1440×3200 (20:9), WebP quality 92, about 0.6–1.3 MB each.
- Keep the middle third calm: widgets sit there.
- New collection: add a function and its palettes in `render.py`, register it in the dict at the
  bottom, preview, then render.
