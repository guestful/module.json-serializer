/**
 * Copyright (C) 2013 Guestful (info@guestful.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guestful.json.groovy;

import groovy.json.JsonDelegate;
import groovy.json.internal.Chr;
import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.util.Expando;

import javax.json.JsonException;
import javax.json.JsonValue;
import java.io.File;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.time.temporal.ChronoField.*;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class GroovyJsonSerializer implements SerializerRepository {

    private static final JsonTypeSerializer TO_STRING = (o, writer) -> writer.writeString(o.toString());
    private static final JsonTypeSerializer TO_ESCAPED_STRING = (o, writer) -> writer.writeEscapedString(o.toString());

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        .withZone(ZoneOffset.UTC);

    private static final DateTimeFormatter ISO_OFFSET_DATE_TIME = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral('T')
        .appendValue(HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(SECOND_OF_MINUTE, 2)
        .appendFraction(MILLI_OF_SECOND, 3, 3, true)
        .appendOffsetId()
        .toFormatter();

    private final Map<Class<?>, JsonTypeSerializer<?>> finalSerializers = new HashMap<>();
    private final Map<Class<?>, JsonTypeSerializer<?>> instanceSerializers = new LinkedHashMap<>();

    private boolean ignoreMapNullValues;

    public GroovyJsonSerializer() {
        addToEscapedStringSerializer(
            URL.class,
            URI.class,
            String.class,
            GString.class,
            StringBuilder.class,
            StringBuffer.class,
            CharSequence.class
        );
        addToStringSerializer(
            UUID.class,
            Locale.class,
            LocalTime.class,
            LocalDate.class
        );

        // finals
        addCustomSerializer(Boolean.class, (o, writer) -> writer.writeBoolean(o));
        addCustomSerializer(Integer.class, (o, writer) -> writer.writeInt(o));
        addCustomSerializer(Long.class, (o, writer) -> writer.writeLong(o));
        addCustomSerializer(Short.class, (o, writer) -> writer.writeShort(o));
        addCustomSerializer(Byte.class, (o, writer) -> writer.writeByte(o));
        addCustomSerializer(BigInteger.class, (o, writer) -> writer.writeBigInteger(o));
        addCustomSerializer(BigDecimal.class, (o, writer) -> writer.writeBigDecimal(o));
        addCustomSerializer(Double.class, (o, writer) -> {
            if (o.isInfinite()) {
                throw new JsonException("Number " + o + " can't be serialized as JSON: infinite are not allowed in JSON.");
            }
            if (o.isNaN()) {
                throw new JsonException("Number " + o + " can't be serialized as JSON: NaN are not allowed in JSON.");
            }
            writer.writeDouble(o);
        });
        addCustomSerializer(Float.class, (o, writer) -> {
            if (o.isInfinite()) {
                throw new JsonException("Number " + o + " can't be serialized as JSON: infinite are not allowed in JSON.");
            }
            if (o.isNaN()) {
                throw new JsonException("Number " + o + " can't be serialized as JSON: NaN are not allowed in JSON.");
            }
            writer.writeFloat(o);
        });
        addCustomSerializer(File.class, (o, writer) -> writer.writeEscapedString(o.toString().replace('\\', '/')));
        addCustomSerializer(Character.class, (o, writer) -> writer.writeEscapedString(Chr.array(o)));
        addCustomSerializer(Pattern.class, (o, writer) -> writer.writeEscapedString(o.pattern()));
        addCustomSerializer(Optional.class, (o, writer) -> {
            if (o.isPresent()) writer.writeObject(o.get());
            else writer.writeNull();
        });
        addCustomSerializer(ZoneOffset.class, (o, writer) -> writer.writeString(o.getId()));
        addCustomSerializer(DayOfWeek.class, (o, writer) -> writer.writeString(o.name().substring(0, 3)));
        addCustomSerializer(Month.class, (o, writer) -> writer.writeString(o.name().substring(0, 3)));
        addCustomSerializer(OffsetDateTime.class, (o, writer) -> writer.writeString(o.format(ISO_OFFSET_DATE_TIME)));
        addCustomSerializer(ZonedDateTime.class, (o, writer) -> writer.writeString(o.toOffsetDateTime().format(ISO_OFFSET_DATE_TIME)));
        addCustomSerializer(Instant.class, (o, writer) -> writer.writeString(UTC_FORMATTER.format(o)));
        addCustomSerializer(Duration.class, (o, writer) -> writer.writeLong(o.toMillis()));
        addCustomSerializer(Period.class, (o, writer) -> writer.writeLong(toDuration(o).toMillis()));

        // instance
        addCustomSerializer(Map.class, (o, writer) -> writer.writeMap(o));
        addCustomSerializer(Iterator.class, (o, writer) -> writer.writeIterator(o));
        addCustomSerializer(Iterable.class, (o, writer) -> writer.writeIterator(o.iterator()));
        addCustomSerializer(Stream.class, (o, writer) -> writer.writeIterator(o.iterator()));
        addCustomSerializer(Enumeration.class, (o, writer) -> writer.writeIterator(new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return o.hasMoreElements();
            }

            @Override
            public Object next() {
                return o.nextElement();
            }
        }));
        addCustomSerializer(JsonValue.class, (o, writer) -> writer.writeJsonValue(o));
        addCustomSerializer(Enum.class, (o, writer) -> writer.writeString(o.name()));
        addCustomSerializer(Number.class, (o, writer) -> writer.writeRaw(o.toString()));
        addCustomSerializer(TimeZone.class, (o, writer) -> writer.writeString(o.getID()));
        addCustomSerializer(ZoneId.class, (o, writer) -> writer.writeString(o.getId()));
        addCustomSerializer(Date.class, (o, writer) -> writer.writeString(UTC_FORMATTER.format(o.toInstant())));
        addCustomSerializer(Calendar.class, (o, writer) -> writer.writeString(UTC_FORMATTER.format(o.toInstant())));
        addCustomSerializer(Closure.class, (o, writer) -> writer.writeMap(JsonDelegate.cloneDelegateAndGetContent(o)));
        addCustomSerializer(Expando.class, (o, writer) -> writer.writeMap(o.getProperties()));
    }

    public <T> GroovyJsonSerializer addCustomSerializer(Class<T> type, JsonTypeSerializer<? super T> serializer) {
        if (Modifier.isFinal(type.getModifiers())) {
            finalSerializers.put(type, serializer);
        } else {
            instanceSerializers.put(type, serializer);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public GroovyJsonSerializer addToStringSerializer(Class<?>... types) {
        for (Class<?> type : types) {
            addCustomSerializer(type, TO_STRING);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public GroovyJsonSerializer addToEscapedStringSerializer(Class<?>... types) {
        for (Class<?> type : types) {
            addCustomSerializer(type, TO_ESCAPED_STRING);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> JsonTypeSerializer<T> findSerializer(Class<?> type) {
        JsonTypeSerializer<?> s = finalSerializers.get(type);
        if (s != null) return (JsonTypeSerializer<T>) s;
        for (Map.Entry<Class<?>, JsonTypeSerializer<?>> entry : instanceSerializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return (JsonTypeSerializer<T>) entry.getValue();
            }
        }
        return null;
    }

    public boolean isIgnoreMapNullValues() {
        return ignoreMapNullValues;
    }

    public GroovyJsonSerializer setIgnoreMapNullValues(boolean ignoreMapNullValues) {
        this.ignoreMapNullValues = ignoreMapNullValues;
        return this;
    }

    public String toJson(Object o) {
        return new JsonWriter(this, ignoreMapNullValues).writeObject(o).toString();
    }

    public String toPrettyJson(Object o) {
        return new JsonWriter(this, ignoreMapNullValues).writeObject(o).toString(true);
    }

    private static Duration toDuration(Period p) {
        if (p.getMonths() > 0) throw new JsonException("Unable to serialize period: " + p.toString());
        if (p.getYears() > 0) throw new JsonException("Unable to serialize period: " + p.toString());
        return Duration.ofDays(p.getDays());
    }

}
