package de.unibi.agbi.biodwh2.core.model.graph;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Edge {
    private static final Logger logger = LoggerFactory.getLogger(Graph.class);

    private final long fromId;
    private final long toId;
    private final String label;
    private final Map<String, Object> properties;

    public Edge(Node from, Node to, String label) {
        fromId = from.getId();
        toId = to.getId();
        this.label = label;
        properties = new HashMap<>();
    }

    public long getFromId() {
        return fromId;
    }

    public long getToId() {
        return toId;
    }

    public String getLabel() {
        return label;
    }

    public Collection<String> getPropertyKeys() {
        return properties.keySet();
    }

    public <T> T getProperty(String key) {
        //noinspection unchecked
        return (T) properties.get(key);
    }

    public void setProperty(String key, Object value) {
        if (value == null)
            properties.put(key, null);
        else {
            Class<?> valueType = value.getClass();
            if (valueType.isArray())
                valueType = valueType.getComponentType();
            if (ClassUtils.isPrimitiveOrWrapper(valueType) || valueType == String.class)
                properties.put(key, value);
            else {
                logger.warn("Type '" + valueType.toString() + "' is not allowed as an edge property. Using the " +
                            "toString representation for now '" + value.toString() + "'");
                properties.put(key, value.toString());
            }
        }
    }
}
