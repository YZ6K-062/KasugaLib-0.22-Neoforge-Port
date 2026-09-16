package kasuga.lib.core.config;

import kasuga.lib.KasugaLib;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;

import org.apache.commons.compress.archivers.sevenz.CLI;

import java.util.HashMap;

public class SimpleConfig {
    private final HashMap<String, ConfigContext<?>> values;
    private ModConfigSpec COMMON, CLIENT, SERVER;
    private final ModConfigSpec.Builder COMMON_BUILDER, CLIENT_BUILDER, SERVER_BUILDER;
    private boolean common_pushed = false, client_pushed = false, server_pushed = false;
    private ModConfigSpec.Builder cachedBuilder;
    public SimpleConfig() {
        COMMON_BUILDER = new ModConfigSpec.Builder();
        CLIENT_BUILDER = new ModConfigSpec.Builder();
        SERVER_BUILDER = new ModConfigSpec.Builder();
        values = new HashMap<>();
        cachedBuilder = COMMON_BUILDER;
    }

    public SimpleConfig client(String key) {
        popIfPushed();
        cachedBuilder = CLIENT_BUILDER;
        cachedBuilder.push(key);
        client_pushed = true;
        return this;
    }

    public SimpleConfig server(String key) {
        popIfPushed();
        cachedBuilder = SERVER_BUILDER;
        cachedBuilder.push(key);
        server_pushed = true;
        return this;
    }

    public SimpleConfig common(String key) {
        popIfPushed();
        cachedBuilder = COMMON_BUILDER;
        cachedBuilder.push(key);
        common_pushed = true;
        return this;
    }

    public SimpleConfig group(String key) {
        popIfPushed();
        cachedBuilder.push(key);
        if (cachedBuilder == COMMON_BUILDER) common_pushed = true;
        else if (cachedBuilder == CLIENT_BUILDER) client_pushed = true;
        else server_pushed = true;
        return this;
    }

    public SimpleConfig comment(String comment) {
        cachedBuilder.comment(comment);
        return this;
    }

    public SimpleConfig intConfig(String key, String comment, Integer defaultValue) {
        ModConfigSpec.ConfigValue<Integer> value = cachedBuilder.comment(comment).define(key, defaultValue);
        values.put(key, new ConfigContext<Integer>(value, Integer.class, key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE));
        return this;
    }

    public SimpleConfig intConfig(String key, Integer defaultValue) {
        ModConfigSpec.ConfigValue<Integer> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<Integer>(value, Integer.class, key, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE));
        return this;
    }

    public SimpleConfig rangedIntConfig(String key, String comment, Integer defaultValue, Integer min, Integer max) {
        ModConfigSpec.IntValue value = cachedBuilder.comment(comment).defineInRange(key, defaultValue, min, max);
        values.put(key, new ConfigContext<Integer>(value, Integer.class, key, defaultValue, min, max));
        return this;
    }

    public SimpleConfig rangedIntConfig(String key, Integer defaultValue, Integer min, Integer max) {
        ModConfigSpec.IntValue value = cachedBuilder.defineInRange(key, defaultValue, min, max);
        values.put(key, new ConfigContext<Integer>(value, Integer.class, key, defaultValue, min, max));
        return this;
    }

    public SimpleConfig doubleConfig(String key, String comment, Double defaultValue) {
        ModConfigSpec.ConfigValue<Double> value = cachedBuilder.comment(comment).define(key, defaultValue);
        values.put(key, new ConfigContext<Double>(value, Double.class, key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE));
        return this;
    }

    public SimpleConfig doubleConfig(String key, Double defaultValue) {
        ModConfigSpec.ConfigValue<Double> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<Double>(value, Double.class, key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE));
        return this;
    }

    public SimpleConfig rangedDoubleConfig(String key, String comment, Double defaultValue, Double min, Double max) {
        ModConfigSpec.DoubleValue value = cachedBuilder.comment(comment).defineInRange(key, defaultValue, min, max);
        values.put(key, new ConfigContext<Double>(value, Double.class, key, defaultValue, min, max));
        return this;
    }

    public SimpleConfig rangedDoubleConfig(String key, Double defaultValue, Double min, Double max) {
        ModConfigSpec.DoubleValue value = cachedBuilder.defineInRange(key, defaultValue, min, max);
        values.put(key, new ConfigContext<Double>(value, Double.class, key, defaultValue, min, max));
        return this;
    }

    public SimpleConfig boolConfig(String key, String comment, Boolean defaultValue) {
        ModConfigSpec.ConfigValue<Boolean> value = cachedBuilder.comment(comment).define(key, defaultValue);
        values.put(key, new ConfigContext<Boolean>(value, Boolean.class, key, defaultValue, null, null));
        return this;
    }

    public <E extends Enum<?>> SimpleConfig enumConfig(String key, String comment, Class<E> clazz, E defaultValue) {
        ModConfigSpec.ConfigValue<E> value = cachedBuilder.comment(comment).define(key, defaultValue);
        values.put(key, new ConfigContext<E>(value, clazz, key, defaultValue, null, null));
        return this;
    }

    public <E extends Enum<?>> SimpleConfig enumConfig(String key, Class<E> clazz, E defaultValue) {
        ModConfigSpec.ConfigValue<E> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<E>(value, clazz, key, defaultValue, null, null));
        return this;
    }

    public SimpleConfig boolConfig(String key, Boolean defaultValue) {
        ModConfigSpec.ConfigValue<Boolean> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<Boolean>(value, Boolean.class, key, defaultValue, null, null));
        return this;
    }

    public SimpleConfig stringConfig(String key, String defaultValue) {
        ModConfigSpec.ConfigValue<String> value = cachedBuilder.define(key, defaultValue);
        values.put(key, new ConfigContext<String>(value, String.class, key, defaultValue, null, null));
        return this;
    }

    public SimpleConfig popCached() {
        popIfPushed();
        return this;
    }
    public SimpleConfig registerConfigs() {
        return registerConfigs(resolveContainer());
    }

    /**
     * 把配置注册到指定 mod 名下（产物为 {@code <modid>-common/client/server.toml}）。
     * <p>
     * 下游 mod 想用自己的文件名时直接调这个重载即可。
     */
    public SimpleConfig registerConfigs(ModContainer container) {
        if (common_pushed) COMMON_BUILDER.pop();
        if (server_pushed) SERVER_BUILDER.pop();
        if (client_pushed) CLIENT_BUILDER.pop();
        common_pushed = false;
        server_pushed = false;
        client_pushed = false;
        COMMON = COMMON_BUILDER.build();
        CLIENT = CLIENT_BUILDER.build();
        SERVER = SERVER_BUILDER.build();
        container.registerConfig(ModConfig.Type.COMMON, COMMON);
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT);
        container.registerConfig(ModConfig.Type.SERVER, SERVER);
        return this;
    }

    /**
     * 原来这里硬编码 {@link KasugaLib#CONTAINER}，导致**任何**下游 mod 调用
     * {@code registerConfigs()} 都会去抢 {@code kasuga_lib-*.toml}，第二个就炸
     * {@code "Detected config file conflict on kasuga_lib-client.toml"}。
     * <p>
     * FML 在 {@code ModLoader#dispatchParallelTask} 里会把当前正在构造的 mod 设成
     * active container，所以正常走 mod 构造期调用时能拿到正确的容器；
     * 拿不到（比如类被提前静态初始化）才退回 KasugaLib 自己的容器。
     */
    private static ModContainer resolveContainer() {
        try {
            ModContainer active = ModLoadingContext.get().getActiveContainer();
            if (active != null) return active;
        } catch (Throwable ignored) {
            // 没有 active container，退回下面的默认容器
        }
        return KasugaLib.CONTAINER;
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public boolean contains(String key, Class<?> clazz) {
        return values.containsKey(key) && values.get(key).clazz == clazz;
    }
    public boolean containsIntValue(String key) {
        return contains(key, Integer.class);
    }

    public boolean isRangedIntValue(String key) {
        if (!containsIntValue(key)) return false;
        return values.get(key).value instanceof ModConfigSpec.IntValue;
    }

    public boolean containsDoubleValue(String key) {
        return contains(key, Double.class);
    }

    public boolean containsBoolValue(String key) {
        return contains(key, Boolean.class);
    }

    public boolean isRangedDoubleValue(String key) {
        if (!containsDoubleValue(key)) return false;
        return values.get(key).value instanceof ModConfigSpec.DoubleValue;
    }

    public boolean containsEnum(String key) {
        return contains(key, Enum.class);
    }

    public Integer getIntValue(String key) {
        if (!containsIntValue(key)) return 0;
        return (Integer) values.get(key).value.get();
    }

    public Boolean getBoolValue(String key) {
        if (!containsBoolValue(key)) return false;
        return (Boolean) values.get(key).value.get();
    }

    public Double getDoubleValue(String key) {
        if (!containsDoubleValue(key)) return 0d;
        return (Double) values.get(key).value.get();
    }

    public <T extends Enum<?>> T getEnumValue(String key) {
        if (!containsEnum(key)) return null;
        return (T) values.get(key).value.get();
    }

    public Integer getDefaultInt(String key) {
        if (!containsIntValue(key)) return 0;
        return (Integer) values.get(key).defaultValue;
    }

    public Double getDefaultDouble(String key) {
        if (!containsDoubleValue(key)) return 0d;
        return (Double) values.get(key).defaultValue;
    }

    public Boolean getDefaultBool(String key) {
        if (!containsBoolValue(key)) return false;
        return (Boolean) values.get(key).defaultValue;
    }

    public <T extends Enum<?>> T getDefaultEnum(String key) {
        if (!containsEnum(key)) return null;
        return (T) values.get(key).defaultValue;
    }

    public Integer getMaxInt(String key) {
        if (!containsIntValue(key)) return Integer.MAX_VALUE;
        return (Integer) values.get(key).max;
    }

    public Integer getMinInt(String key) {
        if (!containsIntValue(key)) return Integer.MIN_VALUE;
        return (Integer) values.get(key).min;
    }

    public Double getMaxDouble(String key) {
        if (!containsDoubleValue(key)) return Double.MAX_VALUE;
        return (Double) values.get(key).max;
    }

    public Double getMinDouble(String key) {
        if (!containsDoubleValue(key)) return Double.MIN_VALUE;
        return (Double) values.get(key).min;
    }

    public String getStringValue(String key) {
        if (!contains(key, String.class)) return "";
        return (String) values.get(key).value.get();
    }

    public ConfigContext<?> getContext(String key) {
        return values.getOrDefault(key, null);
    }

    private void popIfPushed() {
        if (cachedBuilder == COMMON_BUILDER && common_pushed) {
            cachedBuilder.pop();
            common_pushed = false;
        } else if (cachedBuilder == CLIENT_BUILDER && client_pushed) {
            cachedBuilder.pop();
            client_pushed = false;
        } else if (server_pushed) {
            cachedBuilder.pop();
            server_pushed = false;
        }
    }

    private record ConfigContext<T>(ModConfigSpec.ConfigValue<T> value, Class<T> clazz, String key, T defaultValue, T min, T max){}
}
