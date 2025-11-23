package com.example.filme.infra.bully;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HeartbeatWatcher {

    private final BullyElectionService service;
    private final NodesRegistry registry;
    private final RestTemplate restTemplate;
    private final NodeInfo nodeInfo;

    public HeartbeatWatcher(BullyElectionService service, NodesRegistry registry, RestTemplate restTemplate, NodeInfo nodeInfo) {
        this.service = service;
        this.registry = registry;
        this.restTemplate = restTemplate;
        this.nodeInfo = nodeInfo;
    }

    @Scheduled(fixedRate = 5000)
    public void checkCoordinator() {
        Integer coord = service.getCoordinator();
        int myId = nodeInfo.getId();

        if (coord == null) {
            System.out.println("Nenhum coordenador registrado — iniciando eleição!");
            service.startElection();
            return;
        }

        if (myId > coord) {
            System.out.println("ID (" + myId + ") é maior que o coordenador atual (" + coord + ") — iniciando eleição");
            service.startElection();
            return;
        }

        String coordUrl = registry.getNodes().get(coord);
        if (coordUrl == null) {
            System.out.println("Coordenador desconhecido na registry — iniciando eleição");
            service.startElection();
            return;
        }

        try {
            restTemplate.getForObject(coordUrl + "/bully/heartbeat", Boolean.class);
        } catch (Exception e) {
            System.out.println("Coordenador caiu — iniciando eleição");
            service.startElection();
        }
    }
}