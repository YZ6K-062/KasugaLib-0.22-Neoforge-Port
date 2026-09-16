package kasuga.lib.core.client.block_bench_model.json_data;

import org.joml.Vector3f;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public interface IElement {

    String getName();
    Vector3f getPivot();
    int getPreviewColorType();
    UUID getId();
    boolean isExport();
    boolean isLocked();
    boolean isVisibility();
}
