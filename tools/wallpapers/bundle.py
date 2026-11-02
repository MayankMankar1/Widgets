"""Copies wallpapers into the app as resources: full-size WebP plus a small grid thumbnail.

    py -I tools/wallpapers/bundle.py [--extra "E:/100 Widgets Wallpapers"]

- Renders from tools/wallpapers/out/<collection>_<nn>.webp -> wp_<collection>_<nn>.webp (q85)
- Extra images listed in EXTRA below -> wp_scape_<name>.webp at their own size (q90)
- Every wp_* in drawable-nodpi (theme wallpapers too) gets wpt_<same>.webp, 400x889
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
THUMB = (400, 889)


def thumb(src: Image.Image, dest: Path):
    """Centre-crops to the thumbnail's aspect, then downsizes."""
    w, h = src.size
    target = THUMB[0] / THUMB[1]
    if w / h > target:
        cw = round(h * target)
        src = src.crop(((w - cw) // 2, 0, (w - cw) // 2 + cw, h))
    else:
        ch = round(w / target)
        src = src.crop((0, (h - ch) // 2, w, (h - ch) // 2 + ch))
    src.resize(THUMB, Image.LANCZOS).save(dest, "WEBP", quality=82, method=6)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--extra", default="E:/100 Widgets Wallpapers")
    args = ap.parse_args()
    for name in RENDERS:
        Image.open(HERE / "out" / f"{name}.webp").convert("RGB").save(RES / f"wp_{name}.webp", "WEBP", quality=85, method=6)
    for src, name in EXTRA.items():
        Image.open(Path(args.extra) / src).convert("RGB").save(RES / f"wp_{name}.webp", "WEBP", quality=90, method=6)
    total = 0
    for full in sorted(RES.glob("wp_*.webp")):
        dest = RES / f"wpt_{full.stem[3:]}.webp"
        thumb(Image.open(full).convert("RGB"), dest)
        total += full.stat().st_size + dest.stat().st_size
    print(f"{len(list(RES.glob('wp_*.webp')))} wallpapers, {total / 1024 / 1024:.1f} MB with thumbnails")


if __name__ == "__main__":
    main()
