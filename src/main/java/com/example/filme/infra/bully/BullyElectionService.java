package com.example.filme.infra.bully;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BullyElectionService {

    private final NodeInfo nodeInfo;
    private final NodesRegistry registry;
    private final RestTemplate restTemplate;
    private volatile Integer coordinatorId = null;

    public BullyElectionService(NodeInfo nodeInfo, NodesRegistry registry, RestTemplate restTemplate) {
        this.nodeInfo = nodeInfo;
        this.registry = registry;
        this.restTemplate = restTemplate;
        this.recoverFromStartup();
    }

    public void startElection() {
        int myId = nodeInfo.getId();
        boolean higherNodeResponded = false;

        this.coordinatorId = null;

        for (Map.Entry<Integer, String> entry : registry.getNodes().entrySet()) {
            int otherId = entry.getKey();
            String baseUrl = normalizeBaseUrl(entry.getValue());

            if (otherId <= myId) continue;

            try {
                restTemplate.getForObject(baseUrl + "/bully/election", String.class);

                higherNodeResponded = true;
                break;

            } catch (Exception e) {
            }
        }

        if (!higherNodeResponded) {
            announceCoordinator();
        } else {
        }
    }

    private void recoverFromStartup() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Nó iniciado - iniciando eleição de recuperação.");
        startElection();
    }


    private String normalizeBaseUrl(String url) {
        if (url.endsWith("/")) return url.substring(0, url.length() - 1);
        return url;
    }

    public synchronized void announceCoordinator() {
        coordinatorId = nodeInfo.getId();
        for (String baseUrl : registry.getNodes().values()) {
            try {
                restTemplate.postForObject(normalizeBaseUrl(baseUrl) + "/bully/coordinator", coordinatorId, Void.class);
            } catch (Exception ignored) {
            }
        }
        System.out.println("Novo coordenador eleito: " + coordinatorId);
    }

    public synchronized void setCoordinator(Integer id) {
        this.coordinatorId = id;
        System.out.println("Coordenador setado localmente para: " + id);
    }

    public Integer getCoordinator() {
        return coordinatorId;
    }
}