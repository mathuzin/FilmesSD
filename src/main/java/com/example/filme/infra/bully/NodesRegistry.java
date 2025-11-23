package com.example.filme.infra.bully;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NodesRegistry {

    private final Map<Integer, String> nodes = new HashMap<>();

    public NodesRegistry(@Value("${nodes.list}") String nodesList) {
        for (String entry : nodesList.split(",")) {
            entry = entry.trim();
            int sepIndex = entry.indexOf(':');
            int id = Integer.parseInt(entry.substring(0, sepIndex));
            String url = entry.substring(sepIndex + 1).trim();
            if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
            nodes.put(id, url);
        }
    }

    public Map<Integer, String> getNodes() {
        return nodes;
    }
}
