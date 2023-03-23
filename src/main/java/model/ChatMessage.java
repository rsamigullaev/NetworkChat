package model;

import java.util.Date;

public record ChatMessage(
        String userId,
        String username,
        String content,
        Date timestamp
) {
}