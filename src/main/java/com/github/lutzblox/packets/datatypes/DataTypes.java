package com.github.lutzblox.packets.datatypes;

import com.github.lutzblox.exceptions.Errors;
import com.github.lutzblox.exceptions.NetworkException;
import com.github.lutzblox.packets.Packet;
import com.github.lutzblox.packets.datatypes.defaults.*;

import java.util.HashMap;
import java.util.Map;


/**
 * A class that deals with registering and handling {@code DataTypes}
 *
 * @author Christopher Lutz
 */
public class DataTypes {

    private static Map<Class<?>, DataType> dataTypes = new HashMap<Class<?>, DataType>();

    /**
     * Registers a data type to the DataType registry
     *
     * @param type The data type to register
     */
    public static void registerDataType(DataType type) {

        dataTypes.put(type.getTypeClass(), type);
    }

    /**
     * Gets all registered data types
     *
     * @return An array containing all registered data types
     */
    public static DataType[] getDataTypes() {

        return dataTypes.values().toArray(new DataType[]{});
    }

    /**
     * Returns the data type associated with the given class (null if there
     * isn't one)
     *
     * @param c The class to check against
     * @return The DataType registered for the class (can be null if there is no
     * registered data type for the class)
     */
    public static DataType getDataType(Class<?> c) {

        if (c == null) {

            return dataTypes.get(null);
        }

        if (dataTypes.containsKey(c)) {

            return dataTypes.get(c);
        }

        return new StringType();
    }

    /**
     * Gets the data type for the given abbreviation (null if there isn't one)
     *
     * @param abbrev The abbreviation to check against
     * @return The DataType registered for the abbreviation (can be null if
     * there is no registered data type for the abbreviation)
     */
    public static DataType getDataType(String abbrev) {

        for (DataType type : dataTypes.values()) {

            if (type.getAbbreviation().equalsIgnoreCase(abbrev)) {

                return type;
            }
        }

        return new StringType();
    }

    public static String writeType(DataType type, String key, Object value) {

        return type.getAbbreviation().toUpperCase()
                .replace("\n", "$(nl);").replace("\r", "$(cr);")
                + ":"
                + key.replace("\n", "$(nl);")
                .replace("\r", "$(cr);")
                + "="
                + type.writeType(value)
                .replace("\n", "$(nl);")
                .replace("\r", "$(cr);");
    }

    public static DataType readType(String line) {

        if (line.contains("=")) {

            String[] parts = line.split("=", 2);

            if (parts[0].contains(":")) {

                String[] declParts = parts[0].split(":", 2);

                DataType type = DataTypes.getDataType(declParts[0]
                        .replace("$(nl);", "\n").replace("$(cr);", "\r"));

                if (type != null) {

                    return type;

                } else {

                    NullPointerException e = Errors.getMissingDataType("data type abbreviation",
                            declParts[0].replace("$(nl);", "\n").replace("$(cr);", "\r")
                                    .replace("$(vl);", "|").toUpperCase(), new NetworkException(""));

                    throw e;
                }

            } else {

                Errors.unreadableData(new NetworkException(""));
            }

        } else {

            Errors.unreadableData(new NetworkException(""));
        }

        return null;
    }

    public static Packet.PacketData readValue(DataType type, String line) {

        if (line.contains("=")) {

            String[] parts = line.split("=", 2);

            if (parts[0].contains(":")) {

                String value = parts[1];

                String[] declParts = parts[0].split(":", 2);

                if (type != null) {

                    Object parsedValue = type
                            .readType(value.replace("$(nl);", "\n")
                                    .replace("$(cr);", "\r")
                                    .replace("$(vl);", "|"));

                    String key = declParts[1].replace("$(nl);", "\n")
                            .replace("$(cr);", "\r").replace("$(vl);", "|");

                    return new Packet.PacketData(key, parsedValue);

                } else {

                    NullPointerException e = Errors.getMissingDataType("data type abbreviation",
                            declParts[0].replace("$(nl);", "\n").replace("$(cr);", "\r")
                                    .replace("$(vl);", "|").toUpperCase(), new NetworkException(""));

                    throw e;
                }

            } else {

                Errors.unreadableData(new NetworkException(""));
            }

        } else {

            Errors.unreadableData(new NetworkException(""));
        }

        return null;
    }

    /**
     * Register default data types
     */
    public static void registerDefaults() {

        DataTypes.registerDataType(new ShortType());
        DataTypes.registerDataType(new IntegerType());
        DataTypes.registerDataType(new LongType());
        DataTypes.registerDataType(new DoubleType());
        DataTypes.registerDataType(new FloatType());
        DataTypes.registerDataType(new StringType());
        DataTypes.registerDataType(new CharType());
        DataTypes.registerDataType(new BooleanType());
        DataTypes.registerDataType(new ByteType());
        DataTypes.registerDataType(new NullType());
        DataTypes.registerDataType(new StringArrayType());
        DataTypes.registerDataType(new QueryDataType());
    }

    static {

        registerDefaults();
    }
}
