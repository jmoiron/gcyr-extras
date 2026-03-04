#!/usr/bin/env python3
"""
Generate recolored texture assets for GCYR Extras.

This script reads source textures from the bundled gcyr tree and
produces recolored variants for new tiers using a base/highlight
palette mapping. Colors are defined inline for easy tweaking.

Requirements: Pillow (PIL). Install with: pip install Pillow

Usage examples:
  - Generate both motor and tank textures for all tiers:
      python tools/generate_textures.py
  - Only motors, using advanced as the source template:
      python tools/generate_textures.py --kind motor --source-tier advanced
  - Only tanks, to a custom output root:
      python tools/generate_textures.py --kind tank --out-root src/main/resources

Outputs are written under:
  src/main/resources/assets/gcyrextras/textures/block/
    - casings/<tier>_rocket_motor/*.png
    - <tier>_fuel_tank_{side,end}.png

You can adjust colors in MOTOR_TIERS and TANK_TIERS below.
"""

from __future__ import annotations

import argparse
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, Iterable, List, Tuple

try:
    from PIL import Image
except ImportError as e:
    sys.stderr.write(
        "Error: Pillow is required. Install with `pip install Pillow`\n"
    )
    raise


# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

# New motor tier palettes (base/highlight). Adjust freely.
MOTOR_TIERS: Dict[str, Dict[str, Tuple[int, int, int]]] = {
    # Much darker + slightly desaturated sets to match GCYR motors
    # navy -> muted slate blue
    "stellar":   {"base": (0x12, 0x1C, 0x40), "highlight": (0x55, 0x68, 0x94)},
    # deep eggplant -> dusty violet
    "galactic":  {"base": (0x2C, 0x14, 0x42), "highlight": (0x70, 0x58, 0x88)},
    # deep teal-green -> muted seafoam
    "cosmic":    {"base": (0x0C, 0x34, 0x2E), "highlight": (0x4E, 0x70, 0x68)},
    # dark bronze -> muted brass
    "universal": {"base": (0x4A, 0x3C, 0x0A), "highlight": (0x84, 0x6E, 0x2E)},
}

# Separate tank tier palettes — a bit darker/more muted than motors
TANK_TIERS: Dict[str, Dict[str, Tuple[int, int, int]]] = {
    # steel-blue, darker body with muted highlights
    "stellar":   {"base": (0x1E, 0x2E, 0x66), "highlight": (0x6F, 0x85, 0xC9)},
    # deep plum body, violet highlights
    "galactic":  {"base": (0x3E, 0x14, 0x5F), "highlight": (0x8F, 0x6A, 0xC9)},
    # dark pine/teal body, aqua highlights
    "cosmic":    {"base": (0x06, 0x56, 0x46), "highlight": (0x4C, 0xBF, 0xA8)},
    # dark bronze body, brass highlights
    "universal": {"base": (0x70, 0x5A, 0x00), "highlight": (0xC5, 0xAC, 0x46)},
}

# Default source tier within gcyr assets for both motor and tank recolors.
# Can be overridden via --source-tier.
DEFAULT_SOURCE_TIER = "elite"

# Where to read source GCYR textures from (relative to repo root)
GCYR_ASSETS = Path("gcyr/src/main/resources/assets/gcyr/textures")


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def ensure_exists(p: Path) -> None:
    p.parent.mkdir(parents=True, exist_ok=True)


def lerp(a: float, b: float, t: float) -> float:
    return a + (b - a) * t


def lerp_color(c1: Tuple[int, int, int], c2: Tuple[int, int, int], t: float) -> Tuple[int, int, int]:
    return (
        int(round(lerp(c1[0], c2[0], t))),
        int(round(lerp(c1[1], c2[1], t))),
        int(round(lerp(c1[2], c2[2], t))),
    )


def srgb_luma(rgb: Tuple[int, int, int]) -> float:
    # Perceptual luma approximation in sRGB space.
    r, g, b = rgb
    return 0.2126 * r + 0.7152 * g + 0.0722 * b


def build_palette(base: Tuple[int, int, int], highlight: Tuple[int, int, int], steps: int = 32) -> List[Tuple[int, int, int]]:
    """Create a gradient palette from base (dark) to highlight (light).

    Uses a slight ease-in/ease-out to reserve more detail for midtones.
    """
    out: List[Tuple[int, int, int]] = []
    for i in range(steps):
        t = i / max(steps - 1, 1)
        # cubic smoothstep for nicer mid-tones
        t = t * t * (3 - 2 * t)
        out.append(lerp_color(base, highlight, t))
    return out


def recolor_image(img: Image.Image, palette: List[Tuple[int, int, int]]) -> Image.Image:
    """Map image luminance to a provided palette, preserving alpha."""
    if img.mode not in ("RGBA", "RGB"):
        img = img.convert("RGBA")
    has_alpha = (img.mode == "RGBA")
    data = img.getdata()

    # Determine luminance range on visible pixels only
    lum_values: List[float] = []
    for px in data:
        if has_alpha:
            r, g, b, a = px
            if a == 0:
                continue
        else:
            r, g, b = px[:3]
        lum_values.append(srgb_luma((r, g, b)))

    if not lum_values:
        return img.copy()

    lum_min = min(lum_values)
    lum_max = max(lum_values)
    lum_span = max(lum_max - lum_min, 1e-6)
    steps = len(palette)

    def map_px(px):
        if has_alpha:
            r, g, b, a = px
            if a == 0:
                return (0, 0, 0, 0)
            lum = srgb_luma((r, g, b))
            t = (lum - lum_min) / lum_span
            idx = min(steps - 1, max(0, int(round(t * (steps - 1)))))
            nr, ng, nb = palette[idx]
            return (nr, ng, nb, a)
        else:
            r, g, b = px[:3]
            lum = srgb_luma((r, g, b))
            t = (lum - lum_min) / lum_span
            idx = min(steps - 1, max(0, int(round(t * (steps - 1)))))
            nr, ng, nb = palette[idx]
            return (nr, ng, nb)

    new_img = Image.new(img.mode, img.size)
    new_img.putdata([map_px(px) for px in data])
    return new_img


@dataclass
class WorkItem:
    kind: str  # "motor" or "tank"
    tier: str  # e.g., "stellar"


def find_motor_sources(source_tier: str) -> Dict[str, Path]:
    base = GCYR_ASSETS / "block" / "casings" / f"{source_tier}_rocket_motor"
    files = {
        "rocket_motor_top.png": base / "rocket_motor_top.png",
        "rocket_motor_bottom.png": base / "rocket_motor_bottom.png",
        "rocket_motor_side.png": base / "rocket_motor_side.png",
        # Bloom overlays (optional)
        "rocket_motor_side_bloom.png": base / "rocket_motor_side_bloom.png",
        "rocket_motor_bottom_bloom.png": base / "rocket_motor_bottom_bloom.png",
        # mcmeta files (copy-through if present)
        "rocket_motor_side_bloom.mcmeta": base / "rocket_motor_side_bloom.mcmeta",
        "rocket_motor_bottom_bloom.mcmeta": base / "rocket_motor_bottom_bloom.mcmeta",
    }
    return files


def find_tank_sources(source_tier: str) -> Dict[str, Path]:
    base = GCYR_ASSETS / "block"
    files = {
        f"{source_tier}_fuel_tank_end.png": base / f"{source_tier}_fuel_tank_end.png",
        f"{source_tier}_fuel_tank_side.png": base / f"{source_tier}_fuel_tank_side.png",
    }
    return files


def write_image(out_path: Path, img: Image.Image) -> None:
    ensure_exists(out_path)
    img.save(out_path, format="PNG")


def copy_if_exists(src: Path, dst: Path) -> None:
    if src.exists():
        ensure_exists(dst)
        dst.write_bytes(src.read_bytes())


def process_motor(tier: str, source_tier: str, out_root: Path) -> None:
    colors = MOTOR_TIERS[tier]
    palette = build_palette(colors["base"], colors["highlight"], steps=48)
    srcs = find_motor_sources(source_tier)

    # Destination directory mirrors gcyr structure under our mod id
    dest_dir = out_root / "assets" / "gcyrextras" / "textures" / "block" / "casings" / f"{tier}_rocket_motor"

    # Primary textures: recolor base layers, copy bloom overlays unchanged
    base_names = (
        "rocket_motor_top.png",
        "rocket_motor_bottom.png",
        "rocket_motor_side.png",
    )
    bloom_names = (
        "rocket_motor_side_bloom.png",
        "rocket_motor_bottom_bloom.png",
    )

    # Recolor base layers
    for name in base_names:
        src = srcs.get(name)
        if not src or not src.exists():
            raise FileNotFoundError(f"Missing motor source: {src}")
        img = Image.open(src)
        recolored = recolor_image(img, palette)
        write_image(dest_dir / name, recolored)

    # Copy bloom overlays and corresponding mcmeta as-is (if present)
    for name in bloom_names:
        src = srcs.get(name)
        if not src or not src.exists():
            # Optional: some source tiers may omit bloom overlays
            continue
        ensure_exists(dest_dir / name)
        (dest_dir / name).write_bytes(src.read_bytes())

    # Copy mcmeta files unchanged if present
    for meta_name in ("rocket_motor_side_bloom.mcmeta", "rocket_motor_bottom_bloom.mcmeta"):
        meta_src = srcs.get(meta_name)
        if meta_src is None:
            continue
        copy_if_exists(meta_src, dest_dir / meta_name)


def process_tank(tier: str, source_tier: str, out_root: Path) -> None:
    colors = TANK_TIERS[tier]
    palette = build_palette(colors["base"], colors["highlight"], steps=48)
    srcs = find_tank_sources(source_tier)

    dest_dir = out_root / "assets" / "gcyrextras" / "textures" / "block"

    # Output names use the target tier prefix
    mapping = {
        f"{source_tier}_fuel_tank_end.png": f"{tier}_fuel_tank_end.png",
        f"{source_tier}_fuel_tank_side.png": f"{tier}_fuel_tank_side.png",
    }

    for src_name, out_name in mapping.items():
        src = srcs[src_name]
        if not src.exists():
            raise FileNotFoundError(f"Missing tank source: {src}")
        img = Image.open(src)
        recolored = recolor_image(img, palette)
        write_image(dest_dir / out_name, recolored)


def parse_args(argv: Iterable[str]) -> argparse.Namespace:
    p = argparse.ArgumentParser(description="Generate recolored textures for GCYR Extras")
    p.add_argument(
        "--source-tier",
        choices=["basic", "advanced", "elite"],
        default=DEFAULT_SOURCE_TIER,
        help="Which gcyr tier's textures to recolor from",
    )
    p.add_argument(
        "--kind",
        choices=["all", "motor", "tank"],
        default="all",
        help="Which textures to generate",
    )
    p.add_argument(
        "--out-root",
        type=Path,
        default=Path("src/main/resources"),
        help="Output root (mod resources root)",
    )
    p.add_argument(
        "--tiers",
        nargs="*",
        default=list(MOTOR_TIERS.keys()),
        help=f"Subset of tiers to generate (default: {', '.join(MOTOR_TIERS.keys())})",
    )
    return p.parse_args(list(argv))


def main(argv: Iterable[str]) -> int:
    args = parse_args(argv)

    missing_sources: List[Path] = []
    # Probe a couple representative sources to fail fast
    probe_paths = [
        GCYR_ASSETS / "block" / f"{args.source_tier}_fuel_tank_end.png",
        GCYR_ASSETS / "block" / "casings" / f"{args.source_tier}_rocket_motor" / "rocket_motor_side.png",
    ]
    for p in probe_paths:
        if not p.exists():
            missing_sources.append(p)
    if missing_sources:
        sys.stderr.write("Missing expected gcyr source textures:\n")
        for p in missing_sources:
            sys.stderr.write(f"  - {p}\n")
        return 2

    tiers = args.tiers
    for tier in tiers:
        if tier not in MOTOR_TIERS:
            sys.stderr.write(f"Unknown tier '{tier}'. Known: {', '.join(MOTOR_TIERS.keys())}\n")
            return 2

    for tier in tiers:
        if args.kind in ("all", "motor"):
            print(f"[motor] {tier} from {args.source_tier}")
            process_motor(tier, args.source_tier, args.out_root)
        if args.kind in ("all", "tank"):
            print(f"[tank ] {tier} from {args.source_tier}")
            process_tank(tier, args.source_tier, args.out_root)

    print("Done.")
    print(f"Outputs under: {args.out_root}/assets/gcyrextras/textures/block/")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
