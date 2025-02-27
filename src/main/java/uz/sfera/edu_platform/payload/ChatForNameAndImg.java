package uz.sfera.edu_platform.payload;

public record ChatForNameAndImg(
        String senderName,
        String receiverName,
        Long senderImg,
        Long receiverImg
){}
