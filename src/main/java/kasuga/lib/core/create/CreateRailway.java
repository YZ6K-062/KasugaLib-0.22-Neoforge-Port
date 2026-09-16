package kasuga.lib.core.create;

import com.simibubi.create.content.trains.track.TrackMaterial;

import kasuga.lib.core.create.graph.RailwayManager;
import kasuga.lib.registrations.create.TrackMaterialReg;

import java.util.HashMap;

/**
 * Create-flavour holder for the railway data managers.
 *
 * The normal (master) version of KasugaLib does not load Create and therefore has no
 * {@code RAILWAY} field on {@code KasugaLibStacks} / {@code KasugaLibClient}. Since
 * {@code RailwayManager} depends on Create's {@code Train} type, it lives in the gated
 * {@code core/create} source set and cannot be referenced from the always-compiled stacks.
 *
 * This holder therefore provides the server/client managers for the Create flavour only,
 * created lazily on first access so no create-flavour-specific init hook is required.
 */
public class CreateRailway {
    private static RailwayManager SERVER;
    private static RailwayManager CLIENT;
    private static final HashMap<TrackMaterial, TrackMaterialReg> TRACK_MATERIALS = new HashMap<>();

    public static RailwayManager server() {
        if (SERVER == null) SERVER = RailwayManager.createServer();
        return SERVER;
    }

    public static RailwayManager client() {
        if (CLIENT == null) CLIENT = RailwayManager.createClient();
        return CLIENT;
    }

    public static void setServer(RailwayManager manager) {
        SERVER = manager;
    }

    public static void setClient(RailwayManager manager) {
        CLIENT = manager;
    }

    public static void cacheTrackMaterialIn(TrackMaterialReg reg) {
        TRACK_MATERIALS.put(reg.getMaterial(), reg);
    }

    public static TrackMaterialReg getCachedTrackMaterial(TrackMaterial material) {
        return TRACK_MATERIALS.getOrDefault(material, null);
    }
}
