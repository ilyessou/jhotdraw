/*
 * @(#)PreferencesUtil.java
 *
 * Copyright (c) 2005-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.util.prefs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * {@code PreferencesUtil} provides utility methods for {@code
 * java.util.prefs.Preferences}, and can be used as a proxy when the system
 * preferences are not available due to security restrictions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PreferencesUtil
        extends Preferences {

    private HashMap<String, Object> map = new HashMap<String, Object>();
    private boolean isUserNode;
    private static HashMap<Package, Preferences> systemNodes;
    private static HashMap<Package, Preferences> userNodes;

    public PreferencesUtil(boolean isUserNode) {
        this.isUserNode = isUserNode;
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String get(String key, String def) {
        return (String) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public void clear() throws BackingStoreException {
        map.clear();
    }

    @Override
    public void putInt(String key, int value) {
        map.put(key, value);
    }

    @Override
    public int getInt(String key, int def) {
        return (Integer) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putLong(String key, long value) {
        map.put(key, value);
    }

    @Override
    public long getLong(String key, long def) {
        return (Long) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        map.put(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return (Boolean) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putFloat(String key, float value) {
        map.put(key, value);
    }

    @Override
    public float getFloat(String key, float def) {
        return (Float) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putDouble(String key, double value) {
        map.put(key, value);
    }

    @Override
    public double getDouble(String key, double def) {
        return (Double) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        map.put(key, value);
    }

    @Override
    public byte[] getByteArray(String key, byte[] def) {
        return (byte[]) (map.containsKey(key) ? map.get(key) : def);
    }

    @Override
    public String[] keys() throws BackingStoreException {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Override
    public String[] childrenNames() throws BackingStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Preferences parent() {
        return null;
    }

    @Override
    public Preferences node(String pathName) {
        return null;
    }

    @Override
    public boolean nodeExists(String pathName) throws BackingStoreException {
        return false;
    }

    @Override
    public void removeNode() throws BackingStoreException {
        // empty
    }

    @Override
    public String name() {
        return "Dummy";
    }

    @Override
    public String absolutePath() {
        return "Dummy";
    }

    @Override
    public boolean isUserNode() {
        return isUserNode;
    }

    @Override
    public String toString() {
        return "Dummy";
    }

    @Override
    public void flush() throws BackingStoreException {
        clear();
    }

    @Override
    public void sync() throws BackingStoreException {
        //
    }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        //
    }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        //
    }

    @Override
    public void addNodeChangeListener(NodeChangeListener ncl) {
        //
    }

    @Override
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        //
    }

    @Override
    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        //
    }

    @Override
    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        //
    }

    /** Gets the system node for the package of the class if
     * permitted, gets a proxy otherwise.
     *
     * @param c The class
     * @return system node or a proxy.
     */
    public static Preferences systemNodeForPackage(Class<?> c) {
        if (systemNodes != null) {
            if (!systemNodes.containsKey(c.getPackage())) {
                systemNodes.put(c.getPackage(), new PreferencesUtil(false));
            }
            return systemNodes.get(c.getPackage());
        }


        try {
            return Preferences.systemNodeForPackage(c);
        } catch (Throwable t) {
            if (systemNodes == null) {
                systemNodes = new HashMap<>();
            }
            return systemNodeForPackage(c);
        }
    }

    /** Gets the user node for the package of the class if
     * permitted, gets a proxy otherwise.
     *
     * @param c The class
     * @return user node or a proxy.
     */
    public static Preferences userNodeForPackage(Class<?> c) {
        if (userNodes != null) {
            if (!userNodes.containsKey(c.getPackage())) {
                userNodes.put(c.getPackage(), new PreferencesUtil(false));
            }
            return userNodes.get(c.getPackage());
        }

        try {
            return Preferences.userNodeForPackage(c);
        } catch (Throwable t) {
            if (userNodes == null) {
                userNodes = new HashMap<>();
            }
            return userNodeForPackage(c);
        }
    }

    /** Creates a new instance. */
    private PreferencesUtil() {
    }


}
