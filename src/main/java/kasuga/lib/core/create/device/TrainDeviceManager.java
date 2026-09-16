package kasuga.lib.core.create.device;

import com.simibubi.create.content.trains.entity.Train;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-train manager that owns the registered {@link TrainDeviceSystem}s attached
 * to a Create {@link Train}. Device systems are created lazily from their
 * {@link TrainDeviceSystemType} factory and ticked together with the train.
 */
public class TrainDeviceManager {

    private final Train train;
    private final Map<TrainDeviceSystemType<?>, TrainDeviceSystem> systems = new HashMap<>();

    public TrainDeviceManager(Train train) {
        this.train = train;
    }

    public Train getTrain() {
        return train;
    }

    @SuppressWarnings("unchecked")
    public <T extends TrainDeviceSystem> T register(TrainDeviceSystemType<T> type) {
        T system = type.create(this);
        systems.put(type, system);
        return system;
    }

    public TrainDeviceSystem get(TrainDeviceSystemType<?> type) {
        return systems.get(type);
    }

    public void tick() {
        for (TrainDeviceSystem system : systems.values()) {
            system.tick();
        }
    }
}
