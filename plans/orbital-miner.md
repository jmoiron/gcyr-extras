# Orbital Mining Laser Implementation Plan

## Goal

Add a new `gcyr-extras` multiblock, `orbital_mining_laser`, that:

- forms as a GTCEu multiblock
- renders normally when merely formed
- renders custom laser effects only while active
- uses the same GTCEu dynamic-render pipeline as the fusion reactor

The intended active effect is:

- four inward lasers from the north/south/east/west sides of the torus into the central mining core
- one large vertical mining beam emitted downward from the center-bottom of the mining core

## Reference Implementation in GTCEu

GTCEu’s fusion reactor effect is the template.

- The machine definition attaches a dynamic renderer and enables BER rendering in
  [`GTMultiMachines.java`](/home/jmoiron/mc/gcyr-extras/../GregTech-Modern/src/main/java/com/gregtechceu/gtceu/common/data/machines/GTMultiMachines.java:737)
  and
  [`GTMultiMachines.java`](/home/jmoiron/mc/gcyr-extras/../GregTech-Modern/src/main/java/com/gregtechceu/gtceu/common/data/machines/GTMultiMachines.java:739).
- The machine exposes synced render state in
  [`FusionReactorMachine.java`](/home/jmoiron/mc/gcyr-extras/../GregTech-Modern/src/main/java/com/gregtechceu/gtceu/common/machine/multiblock/electric/FusionReactorMachine.java:79).
- The custom renderer lives in
  [`FusionRingRender.java`](/home/jmoiron/mc/gcyr-extras/../GregTech-Modern/src/main/java/com/gregtechceu/gtceu/client/renderer/machine/impl/FusionRingRender.java:28).
- The renderer type is registered on the client in
  [`ClientProxy.java`](/home/jmoiron/mc/gcyr-extras/../GregTech-Modern/src/main/java/com/gregtechceu/gtceu/client/ClientProxy.java:31).

## Current `gcyr-extras` Constraints

`gcyr-extras` currently has:

- plain Forge block/item registration in
  [`GcyrExtras.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/GcyrExtras.java:13)
- a GT addon entrypoint in
  [`GcyrExtrasGTAddon.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/GcyrExtrasGTAddon.java:16)

It does not yet have:

- a `GTRegistrate` holder
- machine registration classes
- client dynamic-render registration

That bootstrap work has to happen first.

## Implementation Checklist

### 1. Add GT registrate bootstrap

Create:

- [`src/main/java/net/jmoiron/gcyrextras/api/registries/GcyrExtrasRegistries.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/api/registries/GcyrExtrasRegistries.java)

Add:

```java
public class GcyrExtrasRegistries {
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(GcyrExtras.MOD_ID);
}
```

Update:

- [`GcyrExtrasGTAddon.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/GcyrExtrasGTAddon.java)

Changes:

- `getRegistrate()` should return `GcyrExtrasRegistries.REGISTRATE`
- `initializeAddon()` should force-load machine registration, for example `GcyrExtrasMachines.init()`

### 2. Add machine registration class

Create:

- [`src/main/java/net/jmoiron/gcyrextras/common/data/GcyrExtrasMachines.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/common/data/GcyrExtrasMachines.java)

Register:

- `public static final MultiblockMachineDefinition ORBITAL_MINING_LASER`

Prototype settings:

- id: `orbital_mining_laser`
- machine class: `OrbitalMiningLaserMachine::new`
- recipe type: `GTRecipeTypes.DUMMY_RECIPES` for renderer bring-up
- rotation: `RotationState.NON_Y_AXIS`
- model: workable casing model with dynamic renderer attached
- BER: enabled with `.hasBER(true)`

### 3. Add machine logic class

Create:

- [`src/main/java/net/jmoiron/gcyrextras/common/machine/multiblock/electric/OrbitalMiningLaserMachine.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/common/machine/multiblock/electric/OrbitalMiningLaserMachine.java)

Base class:

- `WorkableElectricMultiblockMachine`

Initial synced state:

```java
@Getter
@SyncToClient
private boolean laserActive = false;

@Getter
@SyncToClient
private int beamColor = 0xFF44CCFF;
```

Behavior:

- set `laserActive = recipeLogic.isWorking()` while active
- clear it in `onWaiting()`
- clear it in `onStructureInvalid()`
- only call `markClientSyncFieldDirty(...)` when value changes

The first pass does not need custom processing logic. It only needs enough machine state to drive rendering.

### 4. Add client render helper

Create:

- [`src/main/java/net/jmoiron/gcyrextras/client/renderer/machine/GcyrExtrasDynamicRenderHelper.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/client/renderer/machine/GcyrExtrasDynamicRenderHelper.java)

Add:

```java
public static DynamicRender<?, ?> createOrbitalMiningLaserRender() {
    return new OrbitalMiningLaserRender();
}
```

### 5. Add beam render helpers

Create:

- [`src/main/java/net/jmoiron/gcyrextras/client/util/GcyrExtrasRenderBufferHelper.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/client/util/GcyrExtrasRenderBufferHelper.java)

Add helper methods for:

- `renderBeamSegment(...)`
- `renderCrossBeam(...)`
- `renderVerticalBeam(...)`

Implementation guidance:

- inward beams: render as crossed translucent quads between two points
- vertical beam: render as a bright core plus a larger glow shell
- keep the math local to this helper, not inside the renderer class

### 6. Add dynamic renderer

Create:

- [`src/main/java/net/jmoiron/gcyrextras/client/renderer/machine/impl/OrbitalMiningLaserRender.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/client/renderer/machine/impl/OrbitalMiningLaserRender.java)

Base class:

- `DynamicRender<OrbitalMiningLaserMachine, OrbitalMiningLaserRender>`

Required pieces:

- `public static final Codec<OrbitalMiningLaserRender> CODEC = Codec.unit(OrbitalMiningLaserRender::new);`
- `public static final DynamicRenderType<OrbitalMiningLaserMachine, OrbitalMiningLaserRender> TYPE = ...`
- `shouldRender(...)`
- `render(...)`
- `shouldRenderOffScreen(...)`
- `getViewDistance()`
- `getRenderBoundingBox(...)`

Renderer behavior:

- render if `machine.isLaserActive()` or fadeout is still non-zero
- maintain a short fadeout like fusion so the beam does not pop off abruptly
- derive orientation from machine front/up/flipped state
- draw:
  - north beam to center
  - south beam to center
  - west beam to center
  - east beam to center
  - downward vertical beam from emitter to below the machine

### 7. Register the dynamic renderer type on the client

Create:

- [`src/main/java/net/jmoiron/gcyrextras/forge/GcyrExtrasForgeClientEvents.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/forge/GcyrExtrasForgeClientEvents.java)

Register on the mod client event bus:

```java
DynamicRenderManager.register(
    GcyrExtras.id("orbital_mining_laser"),
    OrbitalMiningLaserRender.TYPE
);
```

This mirrors GTCEu’s client registration path.

### 8. Attach renderer in the machine definition

In:

- [`GcyrExtrasMachines.java`](/home/jmoiron/mc/gcyr-extras/src/main/java/net/jmoiron/gcyrextras/common/data/GcyrExtrasMachines.java)

Use:

```java
.model(createWorkableCasingMachineModel(
        GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel"),
        GTCEu.id("block/multiblock/assembly_line"))
        .andThen(b -> b.addDynamicRenderer(
                GcyrExtrasDynamicRenderHelper::createOrbitalMiningLaserRender)))
.hasBER(true)
```

For the prototype, the baked controller model can stay simple. The dynamic renderer carries the feature.

### 9. Add placeholder lang entries

Update:

- [`src/main/resources/assets/gcyrextras/lang/en_us.json`](/home/jmoiron/mc/gcyr-extras/src/main/resources/assets/gcyrextras/lang/en_us.json)

Add:

- block name for `orbital_mining_laser`
- optional tooltip text

### 10. Verify in game

Bring-up checks:

1. structure forms
2. controller model renders correctly
3. idle state has no extra beams
4. active state shows 4 inward beams and 1 vertical beam
5. effect respects rotation
6. beam does not disappear too early due to culling

## Placeholder Structure

## Design Intent

Use a full fusion-sized footprint so the final machine can evolve without replacing the entire spatial layout.

Target placeholder dimensions:

- `15 x 15 x 7`

Interpretation:

- the outer shell is a fusion-scale torus silhouette
- the original `7 x 7 x 7` mining-core placeholder is centered in the exact middle
- only the middle 3 vertical layers contain the torus ring
- the center of the multiblock becomes the renderer’s stable origin

The placeholder is for implementation only. The aisle pattern is expected to change later.

## Block Palette

Use existing blocks first. Do not add new casing content just to get the renderer online.

- `X`: `CASING_TUNGSTENSTEEL_ROBUST`
- `G`: `FUSION_GLASS`
- `B`: `CASING_BEAM_RECEIVER`
- `F`: `frameGt NaquadahAlloy`
- `S`: controller
- `#`: air
- `' '`: any

## Centered 7x7x7 Core

The original mining-core concept should remain a full `7 x 7 x 7` volume, centered inside the larger machine.

Conceptually:

- the core occupies local coordinates `x/z = 4..10` and `y = 0..6`
- the torus occupies only `y = 2..4`
- the core pierces through the center of that torus
- the downward mining beam emits from the center-bottom of this core

## Placeholder Aisles

The following is the implementation placeholder.

Layer 0, bottom cap of mining core:

```text
"               "
"               "
"      BBB      "
"      BBB      "
"      BFB      "
"      BBB      "
"      BBB      "
"      XXX      "
"               "
"               "
"               "
```

Layer 1, lower mining core:

```text
"               "
"               "
"      BBB      "
"      BFB      "
"      B#B      "
"      BFB      "
"      BBB      "
"      XXX      "
"               "
"               "
"               "
"               "
"               "
"               "
"               "
```

Layer 2, lower torus layer plus core:

```text
"      XXX      "
"    XXGGGXX    "
"   XGG###GGX   "
"  XG#######GX  "
" XG##BBB##GX   "
" XG##BFB##GX   "
" XG##B#B##GX   "
" XG##BFB##GX   "
" XG##BBB##GX   "
"  XG#######GX  "
"   XGG###GGX   "
"    XXGGGXX    "
"      XXX      "
"               "
"               "
"               "
```

Layer 3, center torus layer plus core:

```text
"      XXX      "
"    XXGGGXX    "
"   XGG###GGX   "
"  XG#######GX  "
" XG##BBB##GX   "
" XG##BFB##GX   "
"XG###B#B###GX  "
"XG###BFB###GX  "
"XG###BSB###GX  "
" XG##BFB##GX   "
" XG##BBB##GX   "
"  XG#######GX  "
"   XGG###GGX   "
"    XXGGGXX    "
"      XXX      "
```

Layer 4, upper torus layer plus core:

```text
"      XXX      "
"    XXGGGXX    "
"   XGG###GGX   "
"  XG#######GX  "
" XG##BBB##GX   "
" XG##BFB##GX   "
" XG##B#B##GX   "
" XG##BFB##GX   "
" XG##BBB##GX   "
"  XG#######GX  "
"   XGG###GGX   "
"    XXGGGXX    "
"      XXX      "
"               "
"               "
```

Layer 5, upper mining core:

```text
"               "
"               "
"      BBB      "
"      BFB      "
"      B#B      "
"      BFB      "
"      BBB      "
"      XXX      "
"               "
"               "
"               "
"               "
"               "
"               "
"               "
```

Layer 6, top cap of mining core:

```text
"               "
"               "
"      XXX      "
"      BBB      "
"      BBB      "
"      BFB      "
"      BBB      "
"      BBB      "
"               "
"               "
"               "
"               "
"               "
"               "
"               "
```

## Code-Oriented Placeholder Pattern

This is the same placeholder expressed as a candidate `FactoryBlockPattern`.

```java
.pattern(def -> FactoryBlockPattern.start()
    .aisle(
        "               ",
        "               ",
        "      BBB      ",
        "      BBB      ",
        "      BFB      ",
        "      BBB      ",
        "      BBB      ",
        "      XXX      ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ")
    .aisle(
        "               ",
        "               ",
        "      BBB      ",
        "      BFB      ",
        "      B#B      ",
        "      BFB      ",
        "      BBB      ",
        "      XXX      ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ")
    .aisle(
        "      XXX      ",
        "    XXGGGXX    ",
        "   XGG###GGX   ",
        "  XG#######GX  ",
        " XG##BBB##GX   ",
        " XG##BFB##GX   ",
        " XG##B#B##GX   ",
        " XG##BFB##GX   ",
        " XG##BBB##GX   ",
        "  XG#######GX  ",
        "   XGG###GGX   ",
        "    XXGGGXX    ",
        "      XXX      ")
    .aisle(
        "      XXX      ",
        "    XXGGGXX    ",
        "   XGG###GGX   ",
        "  XG#######GX  ",
        " XG##BBB##GX   ",
        " XG##BFB##GX   ",
        "XG###B#B###GX  ",
        "XG###BFB###GX  ",
        "XG###BSB###GX  ",
        " XG##BFB##GX   ",
        " XG##BBB##GX   ",
        "  XG#######GX  ",
        "   XGG###GGX   ",
        "    XXGGGXX    ",
        "      XXX      ")
    .aisle(
        "      XXX      ",
        "    XXGGGXX    ",
        "   XGG###GGX   ",
        "  XG#######GX  ",
        " XG##BBB##GX   ",
        " XG##BFB##GX   ",
        " XG##B#B##GX   ",
        " XG##BFB##GX   ",
        " XG##BBB##GX   ",
        "  XG#######GX  ",
        "   XGG###GGX   ",
        "    XXGGGXX    ",
        "      XXX      ",
        "               ",
        "               ")
    .aisle(
        "               ",
        "               ",
        "      BBB      ",
        "      BFB      ",
        "      B#B      ",
        "      BFB      ",
        "      BBB      ",
        "      XXX      ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ")
    .aisle(
        "               ",
        "               ",
        "      XXX      ",
        "      BBB      ",
        "      BBB      ",
        "      BFB      ",
        "      BBB      ",
        "      BBB      ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ",
        "               ")
    .where('S', controller(blocks(def.getBlock())))
    .where('X', blocks(CASING_TUNGSTENSTEEL_ROBUST.get()).setMinGlobalLimited(40)
        .or(autoAbilities(def.getRecipeTypes())))
    .where('G', blocks(FUSION_GLASS.get()))
    .where('B', blocks(CASING_BEAM_RECEIVER.get()))
    .where('F', blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.NaquadahAlloy)))
    .where('#', air())
    .where(' ', any())
    .build())
```

Notes:

- this should be treated as a renderer scaffold, not the final gameplay structure
- the center mining core is intentionally oversized again, matching the original 7x7x7 concept
- the torus exists only in the middle 3 layers
- the controller is still embedded in the south side of the centered core for easy testing
- if `CASING_BEAM_RECEIVER` feels visually wrong, swap it later without changing the renderer architecture

## Renderer Anchor Coordinates for the Placeholder

Use machine-local coordinates based on the full `15 x 15 x 7` footprint.

Recommended first-pass anchors:

- center target: `(7.5, 3.5, 7.5)`
- north anchor: `(7.5, 3.5, 4.5)`
- south anchor: `(7.5, 3.5, 10.5)`
- west anchor: `(4.5, 3.5, 7.5)`
- east anchor: `(10.5, 3.5, 7.5)`
- downward emitter origin: `(7.5, 0.5, 7.5)`
- downward beam end: `(7.5, -12.0, 7.5)`

These should be kept as named constants in the renderer.

When the aisles change later, the renderer should only need anchor updates if the same conceptual center is preserved.

## Acceptance Criteria

Phase 1 is complete when:

1. the multiblock forms using the placeholder pattern
2. the controller renders normally while idle
3. active state renders four inward beams
4. active state renders the downward mining beam
5. all beams rotate correctly with the machine
6. the beam effect fades out cleanly
7. no client crash occurs from missing dynamic render registration

## Recommended Work Order

1. add `GcyrExtrasRegistries`
2. update `GcyrExtrasGTAddon`
3. add `GcyrExtrasMachines`
4. add `OrbitalMiningLaserMachine`
5. register placeholder multiblock
6. add client render registration
7. add `OrbitalMiningLaserRender`
8. render only the vertical beam first
9. add the four inward beams
10. verify rotation and culling
