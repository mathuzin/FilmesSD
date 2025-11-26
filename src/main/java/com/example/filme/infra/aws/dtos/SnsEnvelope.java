package com.example.filme.infra.aws.dtos;

public record SnsEnvelope(
        String Type,
        String Message
) {
}
