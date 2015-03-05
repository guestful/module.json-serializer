package com.guestful.json.groovy;

/**
* @author Mathieu Carbou (mathieu.carbou@gmail.com)
*/
public interface JsonTypeSerializer<T> {
    void write(T o, JsonWriter writer);
}
