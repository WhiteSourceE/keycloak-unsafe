package org.keycloak;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class Config {

    private static ConfigProvider configProvider = new SystemPropertiesConfigProvider();

    public static void init(ConfigProvider configProvider) {
        Config.configProvider = configProvider;
    }

    public static String getAdminRealm() {
        return configProvider.scope("admin").get("realm", "master");
    }

    public static String getProvider(String spi) {
        String provider = configProvider.getProvider(spi);
        if (provider == null || provider.trim().equals("")) {
            return null;
        } else {
            return provider;
        }
    }

    public static Scope scope(String... scope) {
         return configProvider.scope(scope);
    }

    public static interface ConfigProvider {

        String getProvider(String spi);

        Scope scope(String... scope);

    }

    public static class SystemPropertiesConfigProvider implements ConfigProvider {

        @Override
        public String getProvider(String spi) {
            return System.getProperties().getProperty("keycloak." + spi + ".provider");
        }

        @Override
        public Scope scope(String... scope) {
            StringBuilder sb = new StringBuilder();
            sb.append("keycloak.");
            for (String s : scope) {
                sb.append(s);
                sb.append(".");
            }
            return new SystemPropertiesScope(sb.toString());
        }

    }

    public static class SystemPropertiesScope implements Scope {

        private String prefix;

        public SystemPropertiesScope(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String get(String key) {
            return get(key, null);
        }

        @Override
        public String get(String key, String defaultValue) {
            return System.getProperty(prefix + key, defaultValue);
        }

        @Override
        public String[] getArray(String key) {
            String value = get(key);
            if (value != null) {
                String[] a = value.split(",");
                for (int i = 0; i < a.length; i++) {
                    a[i] = a[i].trim();
                }
                return a;
            } else {
                return null;
            }
        }

        @Override
        public Integer getInt(String key) {
            return getInt(key, null);
        }

        @Override
        public Integer getInt(String key, Integer defaultValue) {
            String v = get(key, null);
            return v != null ? Integer.parseInt(v) : defaultValue;
        }

        @Override
        public Long getLong(String key) {
            return getLong(key, null);
        }

        @Override
        public Long getLong(String key, Long defaultValue) {
            String v = get(key, null);
            return v != null ? Long.parseLong(v) : defaultValue;
        }

        @Override
        public Boolean getBoolean(String key) {
            return getBoolean(key, null);
        }

        @Override
        public Boolean getBoolean(String key, Boolean defaultValue) {
            String v = get(key, null);
            return v != null ? Boolean.parseBoolean(v) : defaultValue;
        }

    }

    /**
     * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
     */
    public static interface Scope {

        String get(String key);

        String get(String key, String defaultValue);

        String[] getArray(String key);

        Integer getInt(String key);

        Integer getInt(String key, Integer defaultValue);

        Long getLong(String key);

        Long getLong(String key, Long defaultValue);

        Boolean getBoolean(String key);

        Boolean getBoolean(String key, Boolean defaultValue);

    }
}
