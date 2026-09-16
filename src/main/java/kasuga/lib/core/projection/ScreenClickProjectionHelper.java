package kasuga.lib.core.projection;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import kasuga.lib.core.util.data_type.Pair;
import kasuga.lib.mixins.mixin.client.MixinGameRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ScreenClickProjectionHelper {
    public static Pair<Vec3, Vec3> getScreenClickProjection(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        GameRenderer renderer = mc.gameRenderer;
        Camera camera = renderer.getMainCamera();
        Window window = mc.getWindow();
        // 1.21: Minecraft#getPartialTick() is gone; the frame timer exposes the partial tick.
        float pt = mc.getTimer().getGameTimeDeltaPartialTick(false);

        Entity camEntity = mc.getCameraEntity() != null ? mc.getCameraEntity() : mc.player;
        camera.setup(mc.level, camEntity,
                !mc.options.getCameraType().isFirstPerson(),
                mc.options.getCameraType().isMirrored(), pt);

        // 1.21: NeoForge's ClientHooks#onCameraSetup was replaced by posting the
        // ViewportEvent.ComputeCameraAngles event on the game event bus.
        ViewportEvent.ComputeCameraAngles camAngles = new ViewportEvent.ComputeCameraAngles(
                camera, pt, camera.getYRot(), camera.getXRot(), camera.getRoll());
        NeoForge.EVENT_BUS.post(camAngles);

        Matrix4f proj = renderer.getProjectionMatrix(
                ((MixinGameRenderer) renderer).kasugalib$invokeGetFov(camera, pt, true)
        );

        PoseStack viewPS = new PoseStack();
        viewPS.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(camAngles.getRoll())));
        viewPS.mulPose(new Quaternionf().rotateX((float) Math.toRadians(camera.getXRot())));
        viewPS.mulPose(new Quaternionf().rotateY((float) Math.toRadians(camera.getYRot() + 180.0F)));
        Vec3 camPos = camera.getPosition();
        viewPS.translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);
        Matrix4f view = viewPS.last().pose();

        Matrix4f vp = new Matrix4f(proj);
        vp.mul(view);
        Matrix4f invVP = new Matrix4f(vp);
        invVP.invert();

        double guiScale = window.getGuiScale();
        double w = window.getWidth();
        double h = window.getHeight();
        boolean isGuiCoords = mouseX <= w / guiScale + 1 && mouseY <= h / guiScale + 1;
        double px = isGuiCoords ? mouseX * guiScale : mouseX;
        double py = isGuiCoords ? mouseY * guiScale : mouseY;

        float ndcX = (float) (px / w * 2.0 - 1.0);
        float ndcY = (float) (-(py / h * 2.0 - 1.0));

        Vector4f near = new Vector4f(ndcX, ndcY, -1.0f, 1.0f);
        near.mul(invVP);
        near.mul(1.0f / near.w());

        Vector4f far = new Vector4f(ndcX, ndcY, 1.0f, 1.0f);
        far.mul(invVP);
        far.mul(1.0f / far.w());

        Vec3 origin = camPos;
        Vec3 dir = new Vec3(far.x() - near.x(), far.y() - near.y(), far.z() - near.z()).normalize();

        return Pair.of(origin, dir);
    }

}
