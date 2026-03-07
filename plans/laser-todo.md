# Laser TODO

## Current State
The renderer was refactored toward the `plans/laser-bloom.md` structure, but the effect is still not visually matching GTCEu fusion in the ways that matter.

Implemented in code:
- custom cylindrical beam geometry
- glow/core render type split
- Shimmer bloom hook via `BloomUtils.entityBloom(...)`
- pattern-space coordinate math
- beacon-style vertical beam stop behavior
- pulse/fade state variables in the renderer
- layered beam rendering helper API

However, several of the planned behaviors are not actually identifiable in-game yet.

## Important Correction
The previous claim that the pulse/fade and other fusion-style effects were already effectively visible was incorrect.

The code contains some of that structure, but the live visual result still looks effectively the same because the most important behavioral differences from fusion are still missing.

## Actual Differences From GTCEu Fusion

1. The renderer is still tied to structure formation, not actual machine work.
- Current orbital renderer uses `machine.isFormed() || delta > 0`
- Fusion uses `machine.recipeLogic.isWorking() || delta > 0`

2. Fade-out is therefore not meaningfully active.
- Current fade only matters when the structure stops being formed
- Fusion fades when the machine stops working

3. The pulse exists in code but is visually buried.
- Only one layer is meaningfully pulsed
- The brightest halo/core layers are fixed white and dominate the look

4. There is still no torus/ring overlay.
- Fusion's signature identity is a glowing torus
- Orbital laser currently has only beams

5. The renderer does not use GTCEu's exact light-ring render type.
- Fusion uses `GTRenderTypes.getLightRing()` with `TRIANGLE_STRIP`
- Orbital uses custom `QUADS` render types because the beam geometry is quad-based

6. Fusion copies the pose stack before bloom routing.
- Orbital currently does not
- This may or may not matter visually, but it is still a difference

## Real Outstanding Work

### 1. Tie the effect to actual machine activity
This is the highest-priority missing behavior.

Needed change:
- replace `machine.isFormed()` gating in `OrbitalMiningLaserRender` with `machine.recipeLogic.isWorking()` or `machine.recipeLogic.isActive()` as appropriate
- keep `delta > 0` so the effect fades after stopping

Expected result:
- beams only show while the machine is actually running
- stop/start transitions become visually meaningful

### 2. Make fade-out actually visible
Once work-state gating is fixed:
- keep `FADEOUT`
- refresh `delta` only while working
- decrement `delta` otherwise

Expected result:
- beams fade after work stops instead of staying at full intensity while the structure remains formed

### 3. Rebalance the layer stack so pulse is visible
Current issue:
- duplicated white halo/core layers swamp the pulse

Needed change:
- pulse more than just one shell layer, or
- reduce fixed white dominance enough that brightness change is identifiable

Expected result:
- the beam should visibly breathe toward white over time instead of reading as a static overbright beam

### 4. Add a faint torus/ring overlay around the mining core
This is still the biggest missing visual callback to fusion.

Implementation direction:
- use GTCEu public `RenderBufferHelper.renderRing(...)`
- place a faint glowing ring around the center mining core while active
- route it through the same bloom path

Expected result:
- the machine reads as part of the same visual family as fusion rather than just “strong beam effect”

### 5. Optional: copy the Shimmer pose-stack handling from fusion
Fusion uses a copied pose stack before bloom routing.

Implementation direction:
- check whether the relevant Shimmer helper is available on compile/runtime classpath
- if so, copy the pose stack before `BloomUtils.entityBloom(...)`
- otherwise keep current path

This is lower priority than activity gating and torus addition.

### 6. Replace deprecated vertical stop test
Current vertical endpoint logic uses deprecated `blocksMotion()`.

Needed change:
- replace with a non-deprecated solid/occlusion/collision test

This is technical cleanup, not the main visual blocker.

## Files To Revisit

- `src/main/java/net/jmoiron/gcyrextras/client/renderer/machine/impl/OrbitalMiningLaserRender.java`
- `src/main/java/net/jmoiron/gcyrextras/client/util/GcyrExtrasRenderBufferHelper.java`
- `src/main/java/net/jmoiron/gcyrextras/client/renderer/GcyrExtrasRenderTypes.java`

Potential addition:
- optional torus/ring helper if `OrbitalMiningLaserRender` gets too crowded

## Recommended Resume Order

1. Gate renderer on `recipeLogic.isWorking()`
2. Verify fade-out now works when the machine stops
3. Rebalance pulse visibility in the layered beam colors
4. Add faint torus overlay with `RenderBufferHelper.renderRing(...)`
5. Do final visual tuning
6. Replace deprecated vertical stop test

## Testing Notes
Only pause for testing when the visual change is obvious.
The next obvious checkpoint should be after:
- activity gating + working fade
- torus overlay
