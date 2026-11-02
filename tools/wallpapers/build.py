"""Renders wallpaper collections with Blender, then adds film grain, writes WebP and a contact sheet.

    py -I tools/wallpapers/build.py glass clay clay_night --count 6
    py -I tools/wallpapers/build.py clay --count 6 --preview      # 25 % size, 16 samples, quick layout check

Needs Blender 5.x (set BLENDER if it isn't in the default install folder), numpy and Pillow.
Output: tools/wallpapers/out/<collection>_<nn>.webp (git-ignored).
"""
import argparse
import os
import subprocess
import time
from pathlib import Path

import numpy as np
from PIL import Image

HERE = Path(__file__).resolve().parent
OUT = HERE / "out"
BLENDER = os.environ.get("BLENDER", r"C:\Program Files\Blender Foundation\Blender 5.2\blender.exe")


def render(collection, variant, png, samples, percent):
    cmd = [BLENDER, "-b", "--factory-startup", "-P", str(HERE / "render.py"), "--",
           collection, str(variant), str(png), str(samples), str(percent)]
    res = subprocess.run(cmd, capture_output=True, text=True, encoding="utf-8", errors="replace")
    if res.returncode != 0 or not png.exists():
        tail = "\n".join((res.stdout + res.stderr).splitlines()[-15:])
        raise RuntimeError(f"{collection} {variant} failed:\n{tail}")


def finish(png, webp, seed, preview):
    a = np.asarray(Image.open(png).convert("RGB"), np.float32) / 255
    if not preview:  # fine grain hides banding in the dark gradients
        a = a + np.random.default_rng(seed).normal(0, 0.012, a.shape[:2] + (1,))
    Image.fromarray((np.clip(a, 0, 1) * 255 + 0.5).astype(np.uint8)).save(webp, quality=92)
    png.unlink()


def sheet(files, path):
    w, h, pad = 240, 533, 14
    s = Image.new("RGB", (len(files) * (w + pad) + pad, h + 2 * pad), (40, 40, 44))
    for i, f in enumerate(files):
        s.paste(Image.open(f).convert("RGB").resize((w, h), Image.LANCZOS), (pad + i * (w + pad), pad))
    s.save(path)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("collections", nargs="+")
    ap.add_argument("--count", type=int, default=6)
    ap.add_argument("--start", type=int, default=0)
    ap.add_argument("--samples", type=int, default=128)
    ap.add_argument("--preview", action="store_true")
    args = ap.parse_args()
    samples, percent = (16, 25) if args.preview else (args.samples, 100)
    OUT.mkdir(exist_ok=True)
    for c in args.collections:
        files = []
        for v in range(args.start, args.start + args.count):
            stem = f"{c}_{v + 1:02d}" + ("_preview" if args.preview else "")
            png, webp = OUT / f"{stem}.png", OUT / f"{stem}.webp"
            t = time.time()
            render(c, v, png, samples, percent)
            finish(png, webp, v, args.preview)
            files.append(webp)
            print(f"{stem}  {time.time() - t:.0f}s  {webp.stat().st_size // 1024} KB", flush=True)
        sheet(files, OUT / f"sheet_{c}{'_preview' if args.preview else ''}.png")


if __name__ == "__main__":
    main()
