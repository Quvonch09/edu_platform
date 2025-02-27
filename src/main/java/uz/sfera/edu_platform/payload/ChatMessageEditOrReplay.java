package uz.sfera.edu_platform.payload;

public record ChatMessageEditOrReplay(
        Long messageId,
        ChatDto chatDto
) {
}
