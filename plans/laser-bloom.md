# Laser Bloom Plan

## Goal
Produce the same visual effect style as GTCEu's fusion ring laser, but implemented entirely inside `gcyr-extras`.

This means:
- strong bloom when Shimmer is present
- cylindrical procedural geometry
- layered color stack with a white-hot core
- color identity in the shell/halo
- pulse toward white over time
- fade-out instead of abrupt disappearance

It does not mean real Minecraft block light emission.

## Reuse From GTCEu
Use GTCEu public classes and methods directly where possible:
- `GTCEu.Mods.isShimmerLoaded()`
- `BloomUtils.entityBloom(...)`
- `RelativeDirection`
- `RelativeDirection.offsetPos(...)`
- `RenderBufferHelper.renderRing(...)` if a torus overlay is added later

Do not patch GTCEu.
Do not rely on non-exposed internals from source only.

## Implementation Steps

1. Replace the current ad hoc beam visual tuning with a dedicated fusion-style beam pipeline.
- Keep `OrbitalMiningLaserRender` as the machine renderer.
- Move all beam visuals behind explicit layered helpers.

2. Keep GTCEu bloom integration.
- Continue using `GTCEu.Mods.isShimmerLoaded()`.
- Continue routing bloom-capable draws through `BloomUtils.entityBloom(...)`.

3. Keep pattern-space coordinate math.
- Continue expressing anchors in pattern coordinates.
- Convert pattern coordinates to renderer-local coordinates once.
- Keep using `RelativeDirection` and `RelativeDirection.offsetPos(...)`.

4. Reuse GTCEu ring rendering where it makes sense.
- `RenderBufferHelper.renderRing(...)` can be used later if a glowing torus effect is added.
- Straight beam cylinders remain custom in `gcyr-extras` because GTCEu does not expose a straight beam helper.

5. Refactor beam geometry helpers.
- Update `GcyrExtrasRenderBufferHelper` to provide explicit helpers for:
  - outer glow
  - bright shell
  - core
  - combined fusion-style beam
- Keep beams cylindrical, not crossed quads.

6. Finalize render types around the actual geometry.
- Keep a translucent `QUADS` render type for the glow shell.
- Keep an additive `QUADS` render type for shell/core/halo.
- Match GTCEu's general render-state style as closely as possible while remaining compatible with quad geometry.

7. Copy fusion's pulse and fade model.
- Add to `OrbitalMiningLaserRender`:
  - `FADEOUT`
  - `delta`
  - `lastColor`
- Use `machine.getOffsetTimer()` to pulse brightness toward white.
- Keep rendering briefly after stop for a fade-out.

8. Split base beam color from frame-to-frame render colors.
- Keep `DEFAULT_BEAM_COLOR` as the identity color.
- Derive each frame:
  - halo/glow color from base color
  - inner shell by lerping toward white
  - core as near-white or white

9. Use the same layered strategy for all beams.
- Horizontal beams and the vertical beam should both use:
  - outer halo
  - inner shell
  - white-hot core
- Only radii and beam length should differ.

10. Preserve the beacon-style vertical endpoint behavior.
- Keep the vertical beam stopping at the first solid block below.
- Keep fallback to the world floor if nothing is hit.
- Isolate this behavior in a helper so visual tuning remains independent.

11. Optionally add a glowing torus later.
- If desired, add a faint active torus around the miner core.
- Use GTCEu's `RenderBufferHelper.renderRing(...)` for that.
- This is optional, but it is the most direct visual callback to fusion.

12. Keep the entire implementation in `gcyr-extras`.
- Reuse GTCEu public APIs.
- Do not modify GTCEu.

## File Plan

Update:
- `src/main/java/net/jmoiron/gcyrextras/client/renderer/machine/impl/OrbitalMiningLaserRender.java`
- `src/main/java/net/jmoiron/gcyrextras/client/util/GcyrExtrasRenderBufferHelper.java`
- `src/main/java/net/jmoiron/gcyrextras/client/renderer/GcyrExtrasRenderTypes.java`

Optional:
- `src/main/java/net/jmoiron/gcyrextras/client/util/GcyrExtrasBeamRenderHelper.java`

## Acceptance Criteria

1. Beams are cylindrical and visually smooth.
2. With Shimmer loaded, the beams bloom strongly like fusion.
3. Without Shimmer, the beams still look layered and energetic.
4. The beam core reads white-hot.
5. The base color reads mainly in the shell/halo.
6. Brightness pulses over time toward white.
7. Stop/start transitions fade instead of popping.
8. The vertical beam still behaves like a beacon-style downward ray.

## Recommended Order

1. Add pulse/fade logic from fusion.
2. Refactor layered beam helpers.
3. Finalize translucent/additive render types.
4. Unify horizontal and vertical layer stacks.
5. Verify Shimmer bloom behavior.
6. Optionally add a faint torus ring.
