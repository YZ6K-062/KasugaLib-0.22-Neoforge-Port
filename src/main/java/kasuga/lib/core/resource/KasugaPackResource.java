package kasuga.lib.core.resource;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class KasugaPackResource extends AbstractPackResources {

    private final HashMap<ResourceLocation, Stack<Resource>> resources;
    private final File file;
    private final List<String> namespaces;
    private final String name;

    public KasugaPackResource(String name, String... namespaces) {
        super(new PackLocationInfo(name, Component.literal(name), PackSource.BUILT_IN, Optional.empty()));
        this.resources = new HashMap<>();
        this.file = new File(name);
        this.namespaces = new ArrayList<>();
        this.namespaces.addAll(Arrays.asList(namespaces));
        this.name = name;
    }

    public boolean registerResource(ResourceLocation location, byte[] data) {
        Resource resource = new Resource(this, () -> new ByteArrayInputStream(data));
        return registerResource(location, resource);
    }

    public boolean registerResource(ResourceLocation location, InputStream stream) throws IOException {
        byte[] data = stream.readAllBytes();
        stream.close();
        return registerResource(location, data);
    }

    public boolean registerResource(ResourceLocation location, File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        return registerResource(location, stream);
    }

    public boolean registerResource(ResourceLocation location, Resource resource) {
        if (!location.getNamespace().equals(resource.sourcePackId())) return false;
        synchronized (namespaces) {
            if (!namespaces.contains(location.getNamespace())) {
                namespaces.add(location.getNamespace());
            }
        }
        synchronized (resources) {
            if (!resources.containsKey(location)) {
                Stack<Resource> stack = new Stack<>();
                stack.push(resource);
                resources.put(location, stack);
                return true;
            }
            Stack<Resource> stack = resources.get(location);
            synchronized (stack) {
                stack.push(resource);
            }
            return true;
        }
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... pFileName) {
        return () -> new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType pType, ResourceLocation pLocation) {
        Stack<Resource> resource = resources.getOrDefault(pLocation, null);
        if (resource == null || resource.isEmpty()) {
            // 1.21: a pack reports "not found" by returning null. FallbackResourceManager relies on
            // this to keep probing the remaining packs — throwing here aborts the whole lookup and
            // turns optional assets (e.g. ModelPreloadManager's anim_model_preload.json) into a
            // fatal client-setup failure.
            return null;
        }
        return resource.peek()::open;
    }

    @Override
    public void listResources(PackType pType, String pNamespace, String pPath, ResourceOutput pOutput) {
        if (!namespaces.contains(pNamespace)) return;
        resources.keySet().stream()
                .filter(location -> location.getNamespace().equals(pNamespace) && location.getPath().startsWith(pPath))
                .forEach(location -> pOutput.accept(location, () -> resources.get(location).peek().open()));
    }


    @Override
    public Set<String> getNamespaces(PackType pType) {
        return new HashSet<>(this.namespaces);
    }

    public Collection<String> getNamespaces() {
        return new ArrayList<>(this.namespaces);
    }

    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> pDeserializer) {
        return null;
    }

    @Override
    public String packId() {
        return name;
    }

    @Override
    public void close() {
    }
}
