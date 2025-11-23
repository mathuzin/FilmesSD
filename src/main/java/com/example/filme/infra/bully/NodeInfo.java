package com.example.filme.infra.bully;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NodeInfo {
    @Value("${node.id}")
    private int nodeId;

    public int getId() {
        return nodeId;
    }
}

