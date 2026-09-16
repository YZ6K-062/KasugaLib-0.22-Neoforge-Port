package kasuga.lib.core.client.model.anim_instance;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Ticker {

    void submit();
    void unload();
    AnimateTicker.TickerType getType();
    void tick(int tick);
}
