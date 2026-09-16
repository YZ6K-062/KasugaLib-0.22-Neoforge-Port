package kasuga.lib.core.client.animation.neo_neo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import kasuga.lib.core.annos.Util;
import kasuga.lib.core.client.render.RendererUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;


@Util
public class VectorUtil {

    public static Vec3 rot(Vector3f axis, Vec3 org, float rot, boolean degree) {
        Vector3f org3f = VectorUtil.vec3ToVec3f(org);
        Quaternionf quaternion = degree ? new Quaternionf().rotationXYZ((float) Math.toRadians(org3f.x()), (float) Math.toRadians(org3f.y()), (float) Math.toRadians(org3f.z())) : new Quaternionf().rotationXYZ(org3f.x(), org3f.y(), org3f.z());
        quaternion.mul(degree ? new Quaternionf().rotateAxis((float) Math.toRadians(rot), axis.x(), axis.y(), axis.z()) : new Quaternionf().rotateAxis(rot, axis.x(), axis.y(), axis.z()));
        return VectorUtil.vec3fToVec3(degree ? quaternion.getEulerAnglesXYZ(new Vector3f()).mul((float) (180.0 / Math.PI)) : quaternion.getEulerAnglesXYZ(new Vector3f()));
    }

    public static Quaternionf rotQuaternion(Quaternionf org, Quaternionf rot) {
        Quaternionf quaternion = new Quaternionf(org);
        quaternion.mul(rot);
        return quaternion;
    }

    public static Vec3 rot(Vec3 org, Vec3 rot, boolean degree) {
        Vector3f org3f = VectorUtil.vec3ToVec3f(org),
                rot3f = VectorUtil.vec3ToVec3f(rot);
        Quaternionf orgQ = degree ? new Quaternionf().rotationXYZ((float) Math.toRadians(org3f.x()), (float) Math.toRadians(org3f.y()), (float) Math.toRadians(org3f.z())) : new Quaternionf().rotationXYZ(org3f.x(), org3f.y(), org3f.z()),
                rotQ = degree ? new Quaternionf().rotationXYZ((float) Math.toRadians(rot3f.x()), (float) Math.toRadians(rot3f.y()), (float) Math.toRadians(rot3f.z())) : new Quaternionf().rotationXYZ(rot3f.x(), rot3f.y(), rot3f.z());
        orgQ.mul(rotQ);
        return VectorUtil.vec3fToVec3(degree ? orgQ.getEulerAnglesXYZ(new Vector3f()).mul((float) (180.0 / Math.PI)) : orgQ.getEulerAnglesXYZ(new Vector3f()));
    }

    public static Quaternionf getQuaternion(Vec3 rot, boolean degree) {
        Vector3f vector3f = VectorUtil.vec3ToVec3f(rot);
        return degree ? new Quaternionf().rotationXYZ((float) Math.toRadians(vector3f.x()), (float) Math.toRadians(vector3f.y()), (float) Math.toRadians(vector3f.z())) : new Quaternionf().rotationXYZ(vector3f.x(), vector3f.y(), vector3f.z());
    }


    public static Vec3 degToRad(Vec3 deg) {
        return deg.scale(Math.PI / 180);
    }

    public static Vec3 radToDeg(Vec3 rad) {
        return rad.scale(180 / Math.PI);
    }

    public static Vec3 rotX(Vec3 org, float rot, boolean degree) {
        return rot(new Vector3f(1, 0, 0), org, rot, degree);
    }

    public static Vec3 rotY(Vec3 org, float rot, boolean degree) {
        return rot(new Vector3f(0, 1, 0), org, rot, degree);
    }

    public static Vec3 rotZ(Vec3 org, float rot, boolean degree) {
        return rot(new Vector3f(0, 0, 1), org, rot, degree);
    }

    public static Vec3 rotXDeg(Vec3 org, float rot) {
        return rotX(org, rot, true);
    }

    public static Vec3 rotYDeg(Vec3 org, float rot) {
        return rotY(org, rot, true);
    }

    public static Vec3 rotZDeg(Vec3 org, float rot) {
        return rotZ(org, rot, true);
    }

    public static Vec3 rotXRad(Vec3 org, float rot) {
        return rotX(org, rot, false);
    }

    public static Vec3 rotYRad(Vec3 org, float rot) {
        return rotY(org, rot, false);
    }

    public static Vec3 rotZRad(Vec3 org, float rot) {
        return rotZ(org, rot, false);
    }



    public static void translate(PoseStack pose, Vec3 vec3) {
        pose.translate(vec3.x(), vec3.y(), vec3.z());
    }

    public static void rot(PoseStack pose, Vec3 rotation, boolean degree) {
        pose.mulPose(getQuaternion(rotation, degree));
    }

    public static float translateDegAndRad(float rot, boolean toDeg) {
        return toDeg ? (rot * 180f / (float) Math.PI) : (rot * (float) Math.PI / 180f);
    }

    public static Vec3 translateDegAndRad(Vec3 rot, boolean toDeg) {
        return toDeg ? rot.scale(180f / Math.PI) : rot.scale(Math.PI / 180f);
    }

    public static void scale(PoseStack pose, Vec3 scale) {
        pose.scale((float) scale.x(), (float) scale.y(), (float) scale.z());
    }

    public static Vec3 normalize(Vec3 vec3) {
        return vec3.scale(1 / vec3.length());
    }

    public static double cosVector(Vec3 vec1, Vec3 vec2) {
        double factor1 = vec1.dot(vec2);
        double factor2 = Math.sqrt(vec2.lengthSqr() * vec1.lengthSqr());
        return factor1 / factor2;
    }

    /**
     * Calculate the right triangle perpendicular vectors of vector 1 and vector 2.
     * @param vec1 hypotenuse.
     * @param vec2 Right angle side 1 of triangle.
     * @return Right angle side 2 of triangle.
     */
    public static Vec3 normalBetween(Vec3 vec1, Vec3 vec2) {
        double cosine = cosVector(vec1, vec2);
        double scale = vec1.length() * cosine;
        Vec3 vec = vec2.scale(scale * vec1.length() / vec2.length());
        return vec1.subtract(vec);
    }

    public static Vec3 mirror(Vec3 axis, Vec3 vector) {
        if (axis.dot(vector) == 0) return vector;
        Vec3 normal = normalBetween(vector, axis);
        return vector.add(normal.scale(2));
    }

    public static Vector3f vec3ToVec3f(Vec3 vec3) {
        return new Vector3f((float) vec3.x(), (float) vec3.y(), (float) vec3.z());
    }

    public static Vec3 vec3fToVec3(Vector3f vector3f) {
        return new Vec3(vector3f);
    }

    public static JsonArray vec3fToJsonArray(Vector3f vector3f) {
        JsonArray array = new JsonArray(3);
        array.add(vector3f.x());
        array.add(vector3f.y());
        array.add(vector3f.z());
        return array;
    }
}
