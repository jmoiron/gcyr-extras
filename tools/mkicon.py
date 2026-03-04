#!/usr/bin/env python3
"""
Compose a 400x400 mod icon from GCYR Extras textures.

By default, this script builds a 2x2 background grid from tank/motor
"side" textures and overlays tools/rocket.png centered on top. The output
defaults to ./icon.png unless overridden.

Usage:
  python tools/mkicon.py [--out OUT.png] [--rocket PATH] [--textures T1 T2 T3 T4]

Notes:
  - Requires Pillow: pip install pillow
  - If --textures are not provided, the script will auto-discover up to four
    background candidates from:
      src/main/resources/assets/gcyrextras/textures/block/
        *_fuel_tank_side.png
        casings/*_rocket_motor/rocket_motor_side.png
"""

from __future__ import annotations

import argparse
import sys
from pathlib import Path

try:
    from PIL import Image
except Exception as e:  # pragma: no cover
    print("Pillow (PIL) is required. Install with: pip install pillow", file=sys.stderr)
    raise


ROOT = Path(__file__).resolve().parents[1]
ASSETS_DIR = ROOT / "src/main/resources/assets/gcyrextras/textures/block"
DEFAULT_ROCKET = ROOT / "tools/rocket.png"


def discover_textures(max_count: int = 4) -> list[Path]:
    candidates: list[Path] = []
    if not ASSETS_DIR.exists():
        return candidates

    # Fuel tank sides
    candidates += sorted(ASSETS_DIR.glob("*_fuel_tank_side.png"))

    # Rocket motor sides under casings/*_rocket_motor/
    candidates += sorted((ASSETS_DIR / "casings").glob("*_rocket_motor/rocket_motor_side.png"))

    # Deduplicate while preserving order
    seen = set()
    unique = []
    for p in candidates:
        if p not in seen:
            unique.append(p)
            seen.add(p)

    # If more than needed, pick first few with a simple interleave strategy:
    # Prefer 2 tanks + 2 motors when possible
    tanks = [p for p in unique if p.name.endswith("_fuel_tank_side.png")]
    motors = [p for p in unique if p.name == "rocket_motor_side.png"]
    if not motors:
        # fallback: any path under casings
        motors = [p for p in unique if "/casings/" in str(p).replace("\\", "/")]

    picked: list[Path] = []
    if tanks or motors:
        picked += tanks[:2]
        picked += motors[:2]
    if len(picked) < max_count:
        for p in unique:
            if p not in picked:
                picked.append(p)
                if len(picked) >= max_count:
                    break
    return picked[:max_count]


def paste_centered(bg: Image.Image, fg: Image.Image) -> None:
    bx, by = bg.size
    fx, fy = fg.size
    x = (bx - fx) // 2
    y = (by - fy) // 2
    bg.alpha_composite(fg, (x, y))


def build_icon(out_path: Path, rocket_path: Path, textures: list[Path]) -> None:
    size = 400
    tile = size // 2  # 200

    # Create transparent RGBA canvas
    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))

    # Load, scale, and place 4 tiles (2x2 grid)
    quads = [(0, 0), (tile, 0), (0, tile), (tile, tile)]
    for i in range(4):
        src = textures[i % len(textures)]
        img = Image.open(src).convert("RGBA").resize((tile, tile), Image.NEAREST)
        canvas.alpha_composite(img, quads[i])

    # Load rocket overlay
    rocket = Image.open(rocket_path).convert("RGBA")

    # Scale rocket to fit within a margin (keep aspect)
    max_w = int(size * 0.75)
    max_h = int(size * 0.75)
    rw, rh = rocket.size
    scale = min(max_w / rw, max_h / rh, 1.0)
    new_size = (max(1, int(rw * scale)), max(1, int(rh * scale)))
    rocket = rocket.resize(new_size, Image.LANCZOS)

    # Optional subtle drop shadow if shadow file exists
    shadow_path = rocket_path.with_name("rocket-shadow.png")
    if shadow_path.exists():
        try:
            shadow = Image.open(shadow_path).convert("RGBA")
            sw, sh = shadow.size
            sscale = min(max_w / sw, max_h / sh, 1.0)
            shadow = shadow.resize((max(1, int(sw * sscale)), max(1, int(sh * sscale))), Image.LANCZOS)
            # Place shadow slightly offset
            tmp = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
            paste_centered(tmp, shadow)
            canvas = Image.alpha_composite(canvas, tmp)
        except Exception:
            pass

    # Paste rocket centered
    tmp = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    paste_centered(tmp, rocket)
    canvas = Image.alpha_composite(canvas, tmp)

    # Ensure output directory exists
    out_path.parent.mkdir(parents=True, exist_ok=True)
    canvas.save(out_path)
    print(f"Wrote icon: {out_path}")


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description="Build a 400x400 GCYR Extras icon")
    parser.add_argument("--out", type=Path, default=ROOT / "icon.png", help="Output image path (default: ./icon.png)")
    parser.add_argument("--rocket", type=Path, default=DEFAULT_ROCKET, help="Path to rocket overlay PNG (default: tools/rocket.png)")
    parser.add_argument("--textures", nargs="*", type=Path, help="Explicit list of 1-4 background textures")
    args = parser.parse_args(argv)

    textures: list[Path]
    if args.textures:
        textures = [p if p.is_absolute() else (ROOT / p) for p in args.textures]
    else:
        textures = discover_textures()

    textures = [p for p in textures if p.exists()]
    if not textures:
        print("No background textures found. Provide --textures or add assets.", file=sys.stderr)
        return 2

    # If fewer than 4 provided/found, they will be cycled
    if not args.rocket.exists():
        print(f"Rocket image not found: {args.rocket}", file=sys.stderr)
        return 2

    build_icon(args.out, args.rocket, textures)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))

