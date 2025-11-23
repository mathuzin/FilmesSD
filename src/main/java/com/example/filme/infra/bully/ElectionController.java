package com.example.filme.infra.bully;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bully")
public class ElectionController {

    private final BullyElectionService service;

    public ElectionController(BullyElectionService service) {
        this.service = service;
    }

    @GetMapping("/election")
    public String receiveElection() {
        service.startElection();
        return "OK";
    }

    @PostMapping("/coordinator")
    public void setCoordinator(@RequestBody Integer id) {
        service.setCoordinator(id);
        System.out.println("Coordenador recebido: " + id);
    }

    @GetMapping("/heartbeat")
    public boolean heartbeat() {
        return true;
    }
}
