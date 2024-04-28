package com.otg.tech.auditingutils.model;

import lombok.Builder;

@Builder
public record AuditingModel(
        String userId,
        String sessionId,
        String auditType,
        String ipAddress,
        String correlationId,
        String deviceId,
        String mobileNumber,
        String persona,
        String emailAddress,
        String request,
        String response,
        String message,
        String channel) {

    public static AuditingModel buildAuditingModel(String userId, String sessionId, String auditType, String ipAddress,
                                                   String correlationId, String deviceId, String mobileNumber, String persona, String emailAddress,
                                                   String request, String response, String message, String channel) {
        return AuditingModel.builder().userId(userId).sessionId(sessionId).auditType(auditType).ipAddress(ipAddress)
                .correlationId(correlationId).deviceId(deviceId).mobileNumber(mobileNumber).persona(persona)
                .emailAddress(emailAddress).request(request).response(response).message(message).channel(channel)
                .build();
    }
}
