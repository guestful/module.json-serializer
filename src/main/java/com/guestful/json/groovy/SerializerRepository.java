package com.guestful.json.groovy;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public interface SerializerRepository {
    public <T> JsonTypeSerializer<T> findSerializer(Class<?> type);
}
