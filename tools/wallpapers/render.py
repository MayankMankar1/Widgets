"""Renders one 3D wallpaper. Runs inside Blender (5.x):

    blender -b --factory-startup -P render.py -- <collection> <variant> <out.png> [samples] [percent]

Every variant has its own palette and a layout drawn from a seeded RNG, so the same
collection + variant always renders the same picture. See README.md next to this file.
"""
import math
import sys

import bpy
import numpy as np
from mathutils import Vector

argv = sys.argv[sys.argv.index("--") + 1:]
COLLECTION, VARIANT, OUT = argv[0], int(argv[1]), argv[2]
SAMPLES = int(argv[3]) if len(argv) > 3 else 128
PERCENT = int(argv[4]) if len(argv) > 4 else 100

bpy.ops.wm.read_factory_settings(use_empty=True)
sc = bpy.context.scene
rng = np.random.default_rng(1000 + VARIANT * 7919 + sum(map(ord, COLLECTION)))

# --- render setup: Cycles on the GPU when possible ---
sc.render.engine = "CYCLES"
prefs = bpy.context.preferences.addons["cycles"].preferences
for kind in ("CUDA", "OPTIX", "HIP", "METAL"):  # CUDA first: OptiX kernels failed to load on the dev laptop
    try:
        prefs.compute_device_type = kind
        (getattr(prefs, "refresh_devices", None) or prefs.get_devices)()
        if any(d.type == kind for d in prefs.devices):
            for d in prefs.devices:
                d.use = d.type == kind
            sc.cycles.device = "GPU"
            break
    except Exception:  # noqa: BLE001 (backend not available on this machine)
        pass
sc.cycles.samples = SAMPLES
sc.cycles.use_denoising = True
sc.cycles.max_bounces = 16
sc.cycles.transmission_bounces = 16
sc.cycles.glossy_bounces = 8
sc.render.resolution_x, sc.render.resolution_y = 1440, 3200
sc.render.resolution_percentage = PERCENT
sc.render.image_settings.file_format = "PNG"
sc.render.filepath = OUT
sc.view_settings.view_transform = "AgX"
for look in ("AgX - Medium High Contrast", "Medium High Contrast"):
    try:
        sc.view_settings.look = look
        break
    except TypeError:
        pass

world = bpy.data.worlds.new("World")
sc.world = world


# --- helpers ---
def lin(hexstr):
    h = hexstr.lstrip("#")
    return tuple(((int(h[i:i + 2], 16) / 255) ** 2.2) for i in (0, 2, 4)) + (1.0,)


def mix(a, b, t):
    """Mix two hex colours in sRGB space."""
    ca = [int(a.lstrip("#")[i:i + 2], 16) for i in (0, 2, 4)]
    cb = [int(b.lstrip("#")[i:i + 2], 16) for i in (0, 2, 4)]
    return "#" + "".join(f"{round(x + (y - x) * t):02X}" for x, y in zip(ca, cb))


def set_world(hexstr, strength):
    world.use_nodes = True
    bg = next(n for n in world.node_tree.nodes if n.type == "BACKGROUND")
    bg.inputs["Color"].default_value = lin(hexstr)
    bg.inputs["Strength"].default_value = strength


def material(name, color="#808080", rough=0.5, transmission=0.0, ior=1.45, emission=None, strength=0.0, coat=0.0):
    m = bpy.data.materials.new(name)
    try:
        m.use_nodes = True
    except Exception:  # noqa: BLE001 (always on in newer versions)
        pass
    nt = m.node_tree
    b = next((n for n in nt.nodes if n.type == "BSDF_PRINCIPLED"), None)
    if b is None:
        b = nt.nodes.new("ShaderNodeBsdfPrincipled")
        out = next(n for n in nt.nodes if n.type == "OUTPUT_MATERIAL")
        nt.links.new(b.outputs[0], out.inputs["Surface"])
    b.inputs["Base Color"].default_value = lin(color)
    b.inputs["Roughness"].default_value = rough
    b.inputs["Transmission Weight"].default_value = transmission
    b.inputs["IOR"].default_value = ior
    b.inputs["Coat Weight"].default_value = coat
    if emission:
        b.inputs["Emission Color"].default_value = lin(emission)
        b.inputs["Emission Strength"].default_value = strength
    return m


def finish(obj, mat, smooth=True):
    obj.data.materials.append(mat)
    if smooth and obj.type == "MESH":
        for p in obj.data.polygons:
            p.use_smooth = True
    return obj


def rad(rot):
    return [math.radians(a) for a in rot]


def sphere(r, loc, mat):
    bpy.ops.mesh.primitive_uv_sphere_add(radius=r, location=loc, segments=128, ring_count=64)
    return finish(bpy.context.object, mat)


def torus(major, minor, loc, rot, mat):
    bpy.ops.mesh.primitive_torus_add(major_radius=major, minor_radius=minor, location=loc, rotation=rad(rot),
                                     major_segments=256, minor_segments=96)
    return finish(bpy.context.object, mat)


def bevelled(o, width):
    mod = o.modifiers.new("bevel", "BEVEL")
    mod.width, mod.segments, mod.limit_method = width, 12, "ANGLE"
    o.modifiers.new("wn", "WEIGHTED_NORMAL")
    return o


def cylinder(r, depth, loc, rot, mat, bevel=0.0):
    bpy.ops.mesh.primitive_cylinder_add(radius=r, depth=depth, location=loc, rotation=rad(rot), vertices=192)
    o = finish(bpy.context.object, mat)
    return bevelled(o, bevel) if bevel else o


def block(size, loc, rot, mat, bevel=0.18):
    bpy.ops.mesh.primitive_cube_add(size=size, location=loc, rotation=rad(rot))
    return bevelled(finish(bpy.context.object, mat, smooth=False), bevel)


def capsule(radius, length, loc, rot, mat):
    bpy.ops.object.metaball_add(type="CAPSULE", radius=radius, location=loc, rotation=rad(rot))
    o = bpy.context.object
    o.data.resolution = 0.05
    o.data.render_resolution = 0.012
    el = o.data.elements[0]
    el.radius = radius
    el.size_x = length / 2
    o.data.materials.append(mat)
    return o


def plane(size, loc, rot, mat):
    bpy.ops.mesh.primitive_plane_add(size=size, location=loc, rotation=rad(rot))
    return finish(bpy.context.object, mat, smooth=False)


def look_at(obj, target):
    obj.rotation_euler = (Vector(target) - obj.location).to_track_quat("-Z", "Y").to_euler()
    return obj


def area_light(loc, target, size, energy, color="#FFFFFF"):
    bpy.ops.object.light_add(type="AREA", location=loc)
    o = bpy.context.object
    o.data.energy = energy
    o.data.color = lin(color)[:3]
    o.data.size = size
    o.visible_camera = False
    return look_at(o, target)


def sun(elevation, azimuth, energy, color, angle=5):
    bpy.ops.object.light_add(type="SUN", rotation=(math.radians(90 - elevation), 0, math.radians(azimuth)))
    o = bpy.context.object
    o.data.energy = energy
    o.data.angle = math.radians(angle)
    o.data.color = lin(color)[:3]
    return o


def camera(loc, target=None, lens=50, ortho=None):
    bpy.ops.object.camera_add(location=loc)
    cam = bpy.context.object
    cam.data.sensor_fit = "VERTICAL"
    cam.data.sensor_height = 36
    if ortho:
        cam.data.type = "ORTHO"
        cam.data.ortho_scale = ortho
    else:
        cam.data.lens = lens
    if target:
        look_at(cam, target)
    sc.camera = cam
    return cam


def pick(seq):
    return seq[int(rng.integers(len(seq)))]


# --- Glass: clear and tinted glass floating in the dark, edges drawn by off-frame light strips ---
# (name, glass tint, key light, light strip)
GLASS_TINTS = [
    ("amber", "#FFB27A", "#FF8A3D", "#FF9B43"),
    ("ice", "#B5DAFF", "#4C9DFF", "#7FC4FF"),
    ("rose", "#FFB3C6", "#FF4F82", "#FF7AA0"),
    ("jade", "#A8F0CF", "#22C983", "#4BE3A0"),
    ("violet", "#CDBBFF", "#7C55FF", "#A585FF"),
    ("mono", "#F4F6FA", "#DDE3EE", "#FFFFFF"),
]
# visible area at y=0 with the glass camera: x within +-1.6, z within +-3.6


def glass():
    name, tint, key, strip = GLASS_TINTS[VARIANT % len(GLASS_TINTS)]
    set_world("#040405", 1.0)
    clear = material("clear", "#FFFFFF", rough=0.03, transmission=1.0, ior=1.5)
    tinted = material("tinted", tint, rough=0.06, transmission=1.0, ior=1.5)
    hero_mat = clear if rng.random() < 0.6 else tinted
    small_mat = tinted if hero_mat is clear else clear

    low = VARIANT % 2 == 0                       # alternate the hero between the lower and upper half
    hz = (-1.5 if low else 1.6) + rng.uniform(-0.25, 0.25)
    hx = rng.uniform(-0.3, 0.3)
    kind = ["torus", "block", "capsule", "torus", "sphere", "ring"][VARIANT % 6]
    tilt = (rng.uniform(45, 75), rng.uniform(-25, 25), rng.uniform(-20, 20))
    if kind == "torus":
        torus(0.88, 0.34, (hx, 0, hz), tilt, hero_mat)
    elif kind == "ring":
        torus(0.95, 0.12, (hx, 0, hz), tilt, hero_mat)
    elif kind == "block":
        block(1.25, (hx, 0, hz), (rng.uniform(20, 50), rng.uniform(10, 40), rng.uniform(0, 45)), hero_mat, bevel=0.3)
    elif kind == "capsule":
        capsule(0.45, 1.6, (hx, 0, hz), (0, rng.uniform(25, 60) * pick([-1, 1]), 0), hero_mat)
    else:
        sphere(0.95, (hx, 0, hz), hero_mat)

    # two or three small spheres in the other half, kept apart from each other
    placed = []
    for _ in range(int(rng.integers(2, 4))):
        for _try in range(40):
            r = rng.uniform(0.12, 0.5)
            x = rng.uniform(-1.2, 1.2)
            z = (rng.uniform(0.4, 2.6) if low else rng.uniform(-2.6, -0.4))
            if all(math.hypot(x - px, z - pz) > r + pr + 0.35 for px, pz, pr in placed):
                placed.append((x, z, r))
                sphere(r, (x, rng.uniform(-0.8, 0.8), z), pick([clear, tinted, small_mat]))
                break

    plane(40, (0, 7, 0), (90, 0, 0), material("backdrop", mix("#0C0C0E", key, 0.04), rough=0.85))
    white = material("strip", "#FFFFFF", emission="#FFFFFF", strength=6)
    coloured = material("strip_col", strip, emission=strip, strength=10)
    side = pick([-1, 1])
    plane(1.0, (3.6 * side, 1.5, 0), (0, 90, 0), coloured).scale = (6, 0.25, 1)
    plane(1.0, (-3.6 * side, 1.5, 0.5), (0, 90, 0), white).scale = (6, 0.18, 1)
    area_light((3.5 * side, 2.5, 3.0), (0, 0, 0), 4, 650, key)
    area_light((0.8 * side, 5.2, hz - 1.6), (hx, 7, hz), 2.5, 260, key)   # soft glow behind the hero
    area_light((-4 * side, -3, 3), (0, 0, 0), 3, 250, "#DDE6FF")
    camera((0, -11, 0), target=(0, 0, 0), lens=55)


# --- Clay: matte shapes on a floor, from above, with long soft shadows. Night: dark floor, low sun ---
# (name, floor, night floor, sun at night, [dominant, second, light, dark, accent])
CLAY_PALETTES = [
    ("terracotta", "#E8E1D6", "#202024", "#FFB070", ["#C5643C", "#D6BF99", "#F4F0E9", "#2B2B2E", "#FF9B43"]),
    ("sage", "#E2E6DB", "#1A201C", "#FFD9A0", ["#7E9A7A", "#CFC6A8", "#F4F2EA", "#33403A", "#E3B65A"]),
    ("ocean", "#DCE3EA", "#141A22", "#A9C8FF", ["#3E6E9E", "#A9C3D6", "#F4F6F8", "#1E2A38", "#F2A65A"]),
    ("blush", "#F0E1DC", "#231A1C", "#FFB8A8", ["#D98C8C", "#EBC9BD", "#FAF3F0", "#4A3438", "#C9A24A"]),
    ("lemon", "#EEEADC", "#1E1D19", "#FFE08A", ["#E8C547", "#D9D2BC", "#FAF8F0", "#2E2E2E", "#3E6E9E"]),
    ("mono", "#E4E4E2", "#1B1B1D", "#FFFFFF", ["#8E8E92", "#BDBDBB", "#F6F6F4", "#262628", "#5A5A5E"]),
]
# visible area with the clay camera: x within +-2.16, y within +-4.8


def clay(night=False):
    name, floor_c, night_floor, night_sun, cols = CLAY_PALETTES[VARIANT % len(CLAY_PALETTES)]
    set_world(mix(night_floor, night_sun, 0.2) if night else "#F2EEE8", 0.3 if night else 0.55)
    plane(60, (0, 0, 0), (0, 0, 0), material("floor", night_floor if night else floor_c, rough=0.9))
    if night:  # dark objects would vanish on a dark floor; lift them to a mid tone
        cols = cols[:3] + [mix(cols[3], "#FFFFFF", 0.45)] + cols[4:]
    mats = [material(f"c{i}", c, rough=0.6) for i, c in enumerate(cols)]

    placed = []

    def free(x, y, r):
        return all(math.hypot(x - px, y - py) > r + pr + 0.3 for px, py, pr in placed)

    def place(kind, band, mat, near=None):
        """Places one shape in a band; small shapes cluster within reach of the big one."""
        for _try in range(240):
            if near and _try < 160:  # fall back to anywhere in the band if the cluster is full
                x, y = near[0] + rng.uniform(-2.0, 2.0), near[1] + rng.uniform(-1.7, 1.7)
            else:
                x, y = rng.uniform(-1.9, 1.9), rng.uniform(*band)
            if not band[0] <= y <= band[1]:
                continue
            if kind == "dome":
                r = rng.uniform(0.95, 1.35)
                foot = r * 0.9
            elif kind == "disc":
                r = rng.uniform(0.6, 0.9)
                foot = r
            elif kind == "block":
                r = rng.uniform(0.8, 1.15)
                foot = r * 0.72
            elif kind == "pill":
                r, length = rng.uniform(0.3, 0.42), rng.uniform(1.4, 2.1)
                foot = length / 2 + r * 0.6
            elif kind == "ring":
                r = rng.uniform(0.42, 0.6)
                foot = r + 0.06
            else:  # ball
                r = rng.uniform(0.22, 0.5)
                foot = r
            bleed = foot * 0.5 if kind in ("dome", "disc") else 0.0   # only big shapes may run off the edge
            if abs(x) + foot > 2.1 + bleed or abs(y) + foot > 4.6 + bleed or not free(x, y, foot):
                continue
            placed.append((x, y, foot))
            if kind == "dome":
                sphere(r, (x, y, 0), mat)
            elif kind == "disc":
                cylinder(r, 0.22, (x, y, 0.11), (0, 0, 0), mat, bevel=0.06)
            elif kind == "block":
                block(r, (x, y, r / 2), (0, 0, rng.uniform(0, 90)), mat)
            elif kind == "pill":
                capsule(r, length, (x, y, r), (0, 0, rng.uniform(0, 180)), mat)
            elif kind == "ring":
                torus(r, 0.06, (x, y, 0.06), (0, 0, 0), mat)
            else:
                sphere(r, (x, y, r), mat)
            return x, y
        return near

    # one large shape per band in a dominant colour, then smaller shapes; the middle stays empty for widgets
    order = [0, 1, 2, 3, 4]
    rng.shuffle(order)
    for band in ((1.9, 4.5), (-4.5, -1.9)):
        hub = place(pick(["dome", "disc", "block"]), band, mats[pick([0, 1])])
        for i in range(int(rng.integers(2, 4))):
            place(pick(["ball", "pill", "ring", "block", "ball"]), band, mats[order[i]], near=hub)

    az = -128 + rng.uniform(-35, 35)
    if night:
        sun(24, az, 3.0, night_sun, angle=4)
    else:
        sun(26, az, 4.2, "#FFF1E0", angle=5)
    camera((0, 0, 30), ortho=9.6)


{"glass": glass, "clay": clay, "clay_night": lambda: clay(night=True)}[COLLECTION]()
bpy.ops.render.render(write_still=True)
print("rendered", OUT)
