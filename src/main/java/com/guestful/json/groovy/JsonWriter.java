package com.guestful.json.groovy;

import groovy.json.JsonLexer;
import groovy.json.JsonToken;
import groovy.json.internal.Chr;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import javax.json.JsonException;
import javax.json.JsonValue;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class JsonWriter {

    private static final char[] EMPTY_MAP_CHARS = {'{', '}'};
    private static final char[] EMPTY_LIST_CHARS = {'[', ']'};
    private static final char[] EMPTY_STRING_CHARS = Chr.array('"', '"');

    private final CharBuf buffer = CharBuf.create(255);

    private final SerializerRepository serializerRepository;
    private final boolean ignoreMapNullValues;

    public JsonWriter(SerializerRepository serializerRepository, boolean ignoreMapNullValues) {
        this.serializerRepository = serializerRepository;
        this.ignoreMapNullValues = ignoreMapNullValues;
    }

    public JsonWriter writeEscapedString(char[] chars) {
        if (chars.length > 0) {
            buffer.addJsonEscapedString(chars);
        } else {
            buffer.addChars(EMPTY_STRING_CHARS);
        }
        return this;
    }

    public JsonWriter writeEscapedString(CharSequence s) {
        if (s.length() > 0) {
            buffer.addJsonEscapedString(s.toString());
        } else {
            buffer.addChars(EMPTY_STRING_CHARS);
        }
        return this;
    }

    public JsonWriter writeRaw(String s) {
        buffer.add(s);
        return this;
    }

    public JsonWriter writeString(CharSequence s) {
        if (s.length() > 0) {
            buffer.addQuoted(s.toString());
        } else {
            buffer.addChars(EMPTY_STRING_CHARS);
        }
        return this;
    }

    public JsonWriter writeBoolean(Boolean o) {
        buffer.addBoolean(o);
        return this;
    }

    public JsonWriter writeDouble(Double key) {
        buffer.addDouble(key);
        return this;
    }

    public JsonWriter writeFloat(Float key) {
        buffer.addFloat(key);
        return this;
    }

    public JsonWriter writeInt(Integer key) {
        buffer.addInt(key);
        return this;
    }

    public JsonWriter writeShort(Short key) {
        buffer.addShort(key);
        return this;
    }

    public JsonWriter writeByte(Byte key) {
        buffer.addByte(key);
        return this;
    }

    public JsonWriter writeLong(Long key) {
        buffer.addLong(key);
        return this;
    }

    public JsonWriter writeBigInteger(BigInteger key) {
        buffer.addBigInteger(key);
        return this;
    }

    public JsonWriter writeBigDecimal(BigDecimal key) {
        buffer.addBigDecimal(key);
        return this;
    }

    public JsonWriter writeNull() {
        buffer.addNull();
        return this;
    }

    public JsonWriter writeJsonValue(JsonValue o) {
        buffer.add(o.toString());
        return this;
    }

    public JsonWriter writeIterator(Iterator<?> o) {
        if (o.hasNext()) {
            buffer.addChar('[');
            Object it = o.next();
            writeObject(it);
            while (o.hasNext()) {
                it = o.next();
                buffer.addChar(',');
                writeObject(it);
            }
            buffer.addChar(']');
        } else {
            buffer.addChars(EMPTY_LIST_CHARS);
        }
        return this;
    }

    public JsonWriter writeArray(Object array) {
        Class<?> arrayClass = array.getClass();
        if (!arrayClass.isArray()) {
            throw new JsonException("not an array: " + arrayClass);
        }
        buffer.addChar('[');
        if (Object[].class.isAssignableFrom(arrayClass)) {
            Object[] objArray = (Object[]) array;
            if (objArray.length > 0) {
                writeObject(objArray[0]);
                for (int i = 1; i < objArray.length; i++) {
                    buffer.addChar(',');
                    writeObject(objArray[i]);
                }
            }
        } else if (int[].class.isAssignableFrom(arrayClass)) {
            int[] intArray = (int[]) array;
            if (intArray.length > 0) {
                buffer.addInt(intArray[0]);
                for (int i = 1; i < intArray.length; i++) {
                    buffer.addChar(',').addInt(intArray[i]);
                }
            }
        } else if (long[].class.isAssignableFrom(arrayClass)) {
            long[] longArray = (long[]) array;
            if (longArray.length > 0) {
                buffer.addLong(longArray[0]);
                for (int i = 1; i < longArray.length; i++) {
                    buffer.addChar(',').addLong(longArray[i]);
                }
            }
        } else if (boolean[].class.isAssignableFrom(arrayClass)) {
            boolean[] booleanArray = (boolean[]) array;
            if (booleanArray.length > 0) {
                buffer.addBoolean(booleanArray[0]);
                for (int i = 1; i < booleanArray.length; i++) {
                    buffer.addChar(',').addBoolean(booleanArray[i]);
                }
            }
        } else if (char[].class.isAssignableFrom(arrayClass)) {
            char[] charArray = (char[]) array;
            if (charArray.length > 0) {
                buffer.addJsonEscapedString(Chr.array(charArray[0]));
                for (int i = 1; i < charArray.length; i++) {
                    buffer.addChar(',').addJsonEscapedString(Chr.array(charArray[i]));
                }
            }
        } else if (double[].class.isAssignableFrom(arrayClass)) {
            double[] doubleArray = (double[]) array;
            if (doubleArray.length > 0) {
                buffer.addDouble(doubleArray[0]);
                for (int i = 1; i < doubleArray.length; i++) {
                    buffer.addChar(',').addDouble(doubleArray[i]);
                }
            }
        } else if (float[].class.isAssignableFrom(arrayClass)) {
            float[] floatArray = (float[]) array;
            if (floatArray.length > 0) {
                buffer.addFloat(floatArray[0]);
                for (int i = 1; i < floatArray.length; i++) {
                    buffer.addChar(',').addFloat(floatArray[i]);
                }
            }
        } else if (byte[].class.isAssignableFrom(arrayClass)) {
            byte[] byteArray = (byte[]) array;
            if (byteArray.length > 0) {
                buffer.addByte(byteArray[0]);
                for (int i = 1; i < byteArray.length; i++) {
                    buffer.addChar(',').addByte(byteArray[i]);
                }
            }
        } else if (short[].class.isAssignableFrom(arrayClass)) {
            short[] shortArray = (short[]) array;
            if (shortArray.length > 0) {
                buffer.addShort(shortArray[0]);
                for (int i = 1; i < shortArray.length; i++) {
                    buffer.addChar(',').addShort(shortArray[i]);
                }
            }
        } else {
            throw new JsonException("Cannot serialize array: " + Arrays.deepToString((Object[]) array));
        }
        buffer.addChar(']');
        return this;
    }

    public JsonWriter writeMap(Map<?, ?> o) {
        if (!o.isEmpty()) {
            buffer.addChar('{');
            boolean firstItem = true;
            for (Map.Entry<?, ?> entry : o.entrySet()) {
                if (ignoreMapNullValues && entry.getValue() == null) {
                    continue;
                }
                if (entry.getKey() == null) {
                    throw new IllegalArgumentException("Maps with null keys can\'t be converted to JSON");
                }
                if (!firstItem) {
                    buffer.addChar(',');
                } else {
                    firstItem = false;
                }
                buffer.addJsonFieldName(entry.getKey().toString());
                writeObject(entry.getValue());
            }
            buffer.addChar('}');
        } else {
            buffer.addChars(EMPTY_MAP_CHARS);
        }
        return this;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean pretty) {
        return pretty ? prettyPrint(buffer.toString()) : buffer.toString();
    }

    public JsonWriter writeObject(Object object) {
        if (object == null) {
            buffer.addNull();
        } else {
            Class<?> objectClass = object.getClass();
            JsonTypeSerializer<Object> serializer = serializerRepository.findSerializer(objectClass);
            if (serializer != null) {
                serializer.write(object, this);
            } else if (objectClass.isArray()) {
                writeArray(object);
            } else {
                Map<?, ?> properties = DefaultGroovyMethods.getProperties(object);
                properties.remove("class");
                properties.remove("declaringClass");
                properties.remove("metaClass");
                writeMap(properties);
            }
        }
        return this;
    }

    @SuppressWarnings("ConstantConditions")
    private static String prettyPrint(String jsonPayload) {
        int indentSize = 0;
        // Just a guess that the pretty view will take a 20 percent more than original.
        final groovy.json.internal.CharBuf output = groovy.json.internal.CharBuf.create((int) (jsonPayload.length() * 0.2));

        JsonLexer lexer = new JsonLexer(new StringReader(jsonPayload));
        // Will store already created indents.
        Map<Integer, char[]> indentCache = new HashMap<>();
        while (lexer.hasNext()) {
            JsonToken token = lexer.next();
            switch (token.getType()) {
                case OPEN_CURLY:
                    indentSize += 4;
                    output.addChars(Chr.array('{', '\n')).addChars(getIndent(indentSize, indentCache));

                    break;
                case CLOSE_CURLY:
                    indentSize -= 4;
                    output.addChar('\n');
                    if (indentSize > 0) {
                        output.addChars(getIndent(indentSize, indentCache));
                    }
                    output.addChar('}');

                    break;
                case OPEN_BRACKET:
                    indentSize += 4;
                    output.addChars(Chr.array('[', '\n')).addChars(getIndent(indentSize, indentCache));

                    break;
                case CLOSE_BRACKET:
                    indentSize -= 4;
                    output.addChar('\n');
                    if (indentSize > 0) {
                        output.addChars(getIndent(indentSize, indentCache));
                    }
                    output.addChar(']');

                    break;
                case COMMA:
                    output.addChars(Chr.array(',', '\n')).addChars(getIndent(indentSize, indentCache));

                    break;
                case COLON:
                    output.addChars(Chr.array(':', ' '));

                    break;
                case STRING:
                    String textStr = token.getText();
                    String textWithoutQuotes = textStr.substring(1, textStr.length() - 1);
                    if (textWithoutQuotes.length() > 0) {
                        output.addJsonEscapedString(textWithoutQuotes);
                    } else {
                        output.addQuoted(Chr.array());
                    }

                    break;
                default:
                    output.addString(token.getText());
            }
        }

        return output.toString();
    }

    private static char[] getIndent(int indentSize, Map<Integer, char[]> indentCache) {
        char[] indent = indentCache.get(indentSize);
        if (indent == null) {
            indent = new char[indentSize];
            Arrays.fill(indent, ' ');
            indentCache.put(indentSize, indent);
        }

        return indent;
    }

}
