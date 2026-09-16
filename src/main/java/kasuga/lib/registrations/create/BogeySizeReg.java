package kasuga.lib.registrations.create;

import com.simibubi.create.content.trains.bogey.BogeySizes;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;

public class BogeySizeReg extends Reg {
    private float size = 1f;
    BogeySizes.BogeySize bogeySize = null;
    public BogeySizeReg(String registrationKey) {
        super(registrationKey);
    }

    public BogeySizeReg size(float size) {
        this.size = size;
        return this;
    }

    @Override
    public BogeySizeReg submit(SimpleRegistry registry) {
        // Create 6.0: BogeySizes.addSize(...) was replaced by constructing a
        // BogeySize record + BogeySizes.register(...). The field `size` is now
        // `wheelRadius` on the record.
        bogeySize = new BogeySizes.BogeySize(registry.asResource(registrationKey), size);
        BogeySizes.register(bogeySize);
        return this;
    }

    public BogeySizes.BogeySize getSize() {
        return bogeySize;
    }

    @Override
    public String getIdentifier() {
        return "bogey_size";
    }
}
