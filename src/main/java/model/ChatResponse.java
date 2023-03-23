package model;

import java.util.Date;

public record ChatResponse(
        Event event,
        String senderId,
        String senderUsername,
        String senderContent,
        Date senderTimestamp
) {

}
