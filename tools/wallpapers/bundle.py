"""Copies wallpapers into the app as resources: full-size WebP plus a small grid thumbnail.

    py -I tools/wallpapers/bundle.py [--extra "E:/100 Widgets Wallpapers"]

- Renders from tools/wallpapers/out/<collection>_<nn>.webp -> wp_<collection>_<nn>.webp (q85)
- Extra images listed in EXTRA below -> wp_scape_<name>.webp at their own size (q90)
- Every wallpaper the grid shows gets wpt_<same>.webp, 540x1200 (theme wallpapers: only those in THEMES)
- Each widget theme gets wpb_<theme>.webp, 900x1350, from the wallpaper in BACKDROPS: the image behind
  widget previews and on the Home scene cards
The app maps them in app/ui/wallpapers/WallpaperCatalog.kt.
"""
import argparse
from pathlib import Path

from PIL import Image

HERE = Path(__file__).resolve().parent
RES = HERE.parents[1] / "app/src/main/res/drawable-nodpi"
RENDERS = [f"{c}_{n:02d}" for c in ("glass", "clay", "clay_night") for n in range(1, 7)]
# source file name -> resource suffix (the user's own landscape images)
EXTRA = {
    "Cherry Blossom Lake at Mount Fuji.png": "scape_cherry_lake",
    "Floating Castle Above the Clouds.png": "scape_floating_castle",
    "Misty Autumn Forest Road.png": "scape_autumn_road",
    "Moonlit Dunes Beneath a Cratered Moon.png": "scape_moonlit_dunes",
    "Sunset Through Concrete Curves.png": "scape_concrete_sunset",
    "Twisted Tree Above Misty Red Valleys.png": "scape_red_valley",
}
THUMB = (540, 1200)  # a 2-column grid tile is ~180 dp wide, ~550 px on xxhdpi
THEMES = ["comic"]  # theme wallpapers offered in the Wallpapers tab
BACKDROP = (900, 1350)
# widget theme -> the wallpaper it is shown on (keep in sync with app/ui/ThemeInfo.kt)
BACKDROPS = {
    "horizon": "scape_cherry_lake",
    "noir": "clay_06",
    "paper": "clay_01",
    "signal": "glass_03",
    "bauhaus": "clay_night_01",
    "comic": "comic",
}


def thumb(src: Image.Image, dest: Path, size=THUMB, quality=82):
    """Centre-crops to the target aspect, then resizes."""
    w, h = src.size
    target = size[0] / size[1]
    if w / h > target:
        cw = round(h * target)
        src = src.crop(((w - cw) // 2, 0, (w - cw) // 2 + cw, h))
    else:
        ch = round(w / target)
        src = src.crop((0, (h - ch) // 2, w, (h - ch) // 2 + ch))
    src.resize(size, Image.LANCZOS).save(dest, "WEBP", quality=quality, method=6)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--extra", default="E:/100 Widgets Wallpapers")
    args = ap.parse_args()
    for name in RENDERS:
        Image.open(HERE / "out" / f"{name}.webp").convert("RGB").save(RES / f"wp_{name}.webp", "WEBP", quality=85, method=6)
    for src, name in EXTRA.items():
        Image.open(Path(args.extra) / src).convert("RGB").save(RES / f"wp_{name}.webp", "WEBP", quality=90, method=6)
    total = 0
    shown = RENDERS + list(EXTRA.values()) + THEMES
    for full in (RES / f"wp_{name}.webp" for name in shown):
        dest = RES / f"wpt_{full.stem[3:]}.webp"
        thumb(Image.open(full).convert("RGB"), dest)
        total += full.stat().st_size + dest.stat().st_size
    for theme, name in BACKDROPS.items():
        dest = RES / f"wpb_{theme}.webp"
        thumb(Image.open(RES / f"wp_{name}.webp").convert("RGB"), dest, BACKDROP, 85)
        total += dest.stat().st_size
    print(f"{len(shown)} wallpapers in the grid, {total / 1024 / 1024:.1f} MB with thumbnails and backdrops")


if __name__ == "__main__":
    main()
