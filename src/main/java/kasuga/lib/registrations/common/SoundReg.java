package kasuga.lib.registrations.common;

import kasuga.lib.core.annos.Mandatory;
import kasuga.lib.registrations.Reg;
import kasuga.lib.registrations.registry.SimpleRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.NotNull;

/**
 * Use this reg to register your custom sound type and sound event to minecraft.
 */
public class SoundReg extends Reg {
    @NotNull public final ResourceLocation soundFile;
    private DeferredHolder<SoundEvent, SoundEvent> registryObject = null;

    /**
     * Create a sound reg.
     * @param registrationKey key of your sound reg.
     * @param soundFileLocation the resource location of your sound file.
     */
    public SoundReg(String registrationKey, @NotNull ResourceLocation soundFileLocation) {
        super(registrationKey);
        this.soundFile = soundFileLocation;
    }

    /**
     * Submit your config to minecraft.
     * @param registry the mod SimpleRegistry.
     * @return self.
     */
    @Override
    @Mandatory
    public SoundReg submit(SimpleRegistry registry) {
        registryObject = registry.sound().register(registrationKey, () -> SoundEvent.createVariableRangeEvent(soundFile));
        return this;
    }

    public DeferredHolder<SoundEvent, SoundEvent> getRegistryObject() {
        return registryObject;
    }

    public SoundEvent getSoundEvent() {
        return registryObject == null ? null : registryObject.get();
    }

    @Override
    public String getIdentifier() {
        return "sound";
    }
}
