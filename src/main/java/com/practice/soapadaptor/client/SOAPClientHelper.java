package com.practice.soapadaptor.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.util.List;

public class SOAPClientHelper {
    private static void traverse(JsonNode node) {
        if (node.getNodeType() == JsonNodeType.OBJECT) {
        } else {
            throw new RuntimeException("Not yet implemented");
        }
    }

    public static void traverseObject(JsonNode node, List<String> list) {
        node.fieldNames().forEachRemaining((String fieldName) -> {
            JsonNode childNode = node.get(fieldName);
            list.add(fieldName);
            if (traversable(childNode)) {
                traverseObject(childNode, list);
            }
        });
    }

    private static boolean traversable(JsonNode node) {
        return node.getNodeType() == JsonNodeType.OBJECT;
    }

}
