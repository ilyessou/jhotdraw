/*
 * @(#)AttributeKey.java  1.2  2007-04-10
 *
 * Copyright (c) 1996-2007 by the original authors of JHotDraw
 * and all its contributors ("JHotDraw.org")
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * JHotDraw.org ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * JHotDraw.org.
 */

package org.jhotdraw.draw;

import java.lang.reflect.*;
import java.util.Map;
import org.jhotdraw.util.Methods;
/**
 * An AttributeKey has a name, a type and a default value. The default value
 * is returned by Figure.getAttribute, if a Figure does not have an attribute
 * of the specified key.
 * <p>
 * An AttributeKey provides typesafe getter and setter for a Figure attribute.
 * The following code example shows how to set and get an attribute on a Figure.
 * <pre>
 * Figure aFigure;
 * AttributeKeys.STROKE_COLOR.set(aFigure, Color.blue);
 * </pre>
 * <p>
 * See {@link AttributeKeys} for a list of useful attribute keys.
 * <p>
 * FIXME AttributeKey must not override equals and hashCode from Object.
 *
 * @author Werner Randelshofer
 * @version 1.2 2007-04-10 Convenience methods for getting and setting a clone
 * of an attribute added. 
 * <br>1.1 2006-12-29 Support for getting/setting attribute keys on a
 * Map added.
 * <br>1.0.1 2006-07-14 Null values are not returned anymore when null
 * values are not allowed. 
 * <br>1.0 7. Juni 2006 Created.
 */
public class AttributeKey<T> {
    private String key;
    private T defaultValue;
    private boolean isNullValueAllowed;
    
    /** Creates a new instance. */
    public AttributeKey(String key) {
        this(key, null, true);
    }
    public AttributeKey(String key, T defaultValue) {
        this(key, defaultValue, true);
    }
    public AttributeKey(String key, T defaultValue, boolean isNullValueAllowed) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.isNullValueAllowed = isNullValueAllowed;
    }
    
    public String getKey() {
        return key;
    }
    public T getDefaultValue() {
        return defaultValue;
    }
    /**
     * Gets a clone of the value from the Figure.
     */
    public T getClone(Figure f) {
        T value = get(f);
        try {
            return value == null ? null : (T) Methods.invoke(value,"clone");
        } catch (NoSuchMethodException ex) {
            InternalError e = new InternalError();
            e.initCause(ex);
            throw e;
        }
    }
    
    public T get(Figure f) {
        T value = (T) f.getAttribute(this);
        return (value == null && ! isNullValueAllowed) ? defaultValue : value;
    }
    public T get(Map<AttributeKey,Object> a) {
        T value = (T) a.get(this);
        return (value == null && ! isNullValueAllowed) ? defaultValue : value;
    }
    
    
    public void set(Figure f, T value) {
        if (value == null && ! isNullValueAllowed) {
            throw new NullPointerException("Null value not allowed for AttributeKey "+key);
        }
        f.setAttribute(this, value);
    }
    public void set(Map<AttributeKey,Object> a, T value) {
        if (value == null && ! isNullValueAllowed) {
            throw new NullPointerException("Null value not allowed for AttributeKey "+key);
        }
        a.put(this, value);
    }
    /**
     * Sets a clone of the value to the Figure.
     */
    public void setClone(Figure f, T value) {
        try {
            set(f, value == null ? null : (T) Methods.invoke(value,"clone"));
        } catch (NoSuchMethodException ex) {
            InternalError e = new InternalError();
            e.initCause(ex);
            throw e;
        }
    }
    
    
    public void basicSet(Figure f, T value) {
        if (value == null && ! isNullValueAllowed) {
            throw new NullPointerException("Null value not allowed for AttributeKey "+key);
        }
        f.basicSetAttribute(this, value);
    }
    /**
     * Sets a clone of the value to the Figure without firing events.
     */
    public void basicSetClone(Figure f, T value) {
        try {
            basicSet(f, value == null ? null : (T) Methods.invoke(value,"clone"));
        } catch (NoSuchMethodException ex) {
            InternalError e = new InternalError();
            e.initCause(ex);
            throw e;
        }
    }
    
    public boolean equals(Object o) {
        if (o instanceof AttributeKey) {
            AttributeKey that = (AttributeKey) o;
            return that.key.equals(this.key);
        }
        return false;
    }
    
    public int hashCode() {
        return key.hashCode();
    }
    
    public String toString() {
        return key;
    }
    
    public boolean isNullValueAllowed() {
        return isNullValueAllowed;
    }
    
    public boolean isAssignable(Object value) {
        if (value == null) {
            return isNullValueAllowed();
        }
        
        // XXX - This works, but maybe there is an easier way to do this?
        try {
            T a = (T) value;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
}