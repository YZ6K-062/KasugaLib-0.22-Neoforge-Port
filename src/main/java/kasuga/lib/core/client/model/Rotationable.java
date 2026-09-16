package kasuga.lib.core.client.model;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface Rotationable {
    Vector3f getPivot();
    Vector3f getRotation();
    boolean hasParent();
    Rotationable getParent();

    default RotationContext compileRotate(RotationContext context) {
        if (hasParent())
            context = getParent().compileRotate(context);
        Vector3f offset = new Vector3f(this.getPivot());
        offset.sub(context.lastPivot());
        context.quaternions.forEach(offset::rotate);
        context.position.add(offset);

        if (!getRotation().equals(new Vector3f(0, 0, 0))) {
            Vector3f rotation = new Vector3f(getRotation());
            rotation.mul(-1, -1, 1);
            Quaternionf cubeRot = new Quaternionf();
            rotQuaternion(cubeRot, rotation);
            context.quaternions.add(0, cubeRot);
        }

        Vector3f pivot = new Vector3f(this.getPivot());
        return new RotationContext(context.position, pivot, context.quaternions);
    }

    default RotationContext startCompileRotate() {
        return this.compileRotate(
                new RotationContext(
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 0, 0),
                        new LinkedList<>()
                )
        );
    }

    default Vector3f getNearestValidPivot() {
        if (!this.getRotation().equals(new Vector3f(0, 0, 0))) return this.getPivot();
        if (this.hasParent()) return this.getParent().getNearestValidPivot();
        return new Vector3f(0, 0, 0);
    }

    private void rotQuaternion(Quaternionf quaternion, Vector3f rotDeg) {
        quaternion.mul(new Quaternionf().rotateZ((float) Math.toRadians(rotDeg.z())));
        quaternion.mul(new Quaternionf().rotateY((float) Math.toRadians(rotDeg.y())));
        quaternion.mul(new Quaternionf().rotateX((float) Math.toRadians(rotDeg.x())));
    }


    record RotationContext(Vector3f position, Vector3f lastPivot, List<Quaternionf> quaternions) {}
}
