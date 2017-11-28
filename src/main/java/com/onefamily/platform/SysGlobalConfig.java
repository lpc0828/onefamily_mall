package com.onefamily.platform;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SysGlobalConfig {
    private static Logger log = LoggerFactory.getLogger(SysGlobalConfig.class);
    private static PropertiesConfiguration config = null;
    private static String GLOBLE_SYSCONFIG_FILE = "syscfg.properties";

    static {
        try {
            config = new PropertiesConfiguration();
            config.setEncoding("UTF-8");
            config.load(GLOBLE_SYSCONFIG_FILE);
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
            config.setThrowExceptionOnMissing(false);
        } catch (ConfigurationException e) {
            log.error("error", e);
        }
    }

    private static class SysGlobalConfigHolder {
        private static SysGlobalConfig instance = new SysGlobalConfig();
    }

    /**
     * 实现单例模式
     *
     * @return
     */
    public static SysGlobalConfig getInstance() {
        return SysGlobalConfigHolder.instance;
    }

    public String getString(String key) {
        return config == null ? null : config.getString(key);
    }

    public String getString(String key, String defaultValue) {
        return config == null ? defaultValue : config.getString(key, defaultValue);
    }

    public int getInt(String key) {
        return config.getInt(key);
    }

    public int getInt(String key, int defaultValue) {
        return config == null ? defaultValue : config.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return config == null ? defaultValue : config.getBoolean(key, defaultValue);
    }

    public double getDouble(String key) {
        return config.getDouble(key);
    }

    public double getDouble(String key, double defaultValue) {
        return config == null ? defaultValue : config.getDouble(key, defaultValue);
    }

    public float getFloat(String key) {
        return config.getFloat(key);
    }

    public float getFloat(String key, float defaultValue) {
        return config == null ? defaultValue : config.getFloat(key, defaultValue);
    }

    public long getLong(String key) {
        return config.getLong(key);
    }

    public long getLong(String key, long defaultValue) {
        return config == null ? defaultValue : config.getLong(key, defaultValue);
    }

    public short getShort(String key) {
        return config.getShort(key);
    }

    public short getShort(String key, short defaultValue) {
        return config == null ? defaultValue : config.getShort(key, defaultValue);
    }

    public List<Object> getList(String key) {
        return config == null ? null : config.getList(key);
    }

    public List<Object> getList(String key, List<Object> defaultValue) {
        return config == null ? defaultValue : config.getList(key, defaultValue);
    }

    public byte getByte(String key) {
        return config.getByte(key);
    }

    public byte getByte(String key, byte defaultValue) {
        return config == null ? defaultValue : config.getByte(key, defaultValue);
    }

    public String[] getStringArray(String key) {
        return config == null ? null : config.getStringArray(key);
    }

}
