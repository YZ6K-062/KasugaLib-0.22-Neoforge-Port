package kasuga.lib.core.compat.iris;

// NOTE: Iris/Oculus compat is currently a no-op stub. The original implementation
// referenced net.coderbot.iris internals (BlockRenderingSettings / ShadowRenderingState)
// which no longer exist under that package in Iris 1.8 (renamed to net.irisshaders).
// Restore the real integration once the new Iris API is ported.
public class IrisOculusCompatImpl implements IrisOculusCompat {
    public IrisOculusCompatImpl() {}

    @Override
    public boolean isRenderingShadow() {
        return false;
    }

    protected Boolean isUsingExtendedVertexFormat;

    @Override
    public void pushExtendedVertexFormat(boolean newValue) {
        if (isUsingExtendedVertexFormat != null)
            throw new IllegalStateException("Already in extended vertex bypass mode!");
        isUsingExtendedVertexFormat = false;
    }

    @Override
    public void popExtendedVertexFormat() {
        if (isUsingExtendedVertexFormat == null)
            throw new IllegalStateException("Not in extended vertex bypass mode!");
        isUsingExtendedVertexFormat = null;
    }
}
