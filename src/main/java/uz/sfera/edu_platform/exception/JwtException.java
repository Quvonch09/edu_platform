package uz.sfera.edu_platform.exception;

public class JwtException extends RuntimeException{
    public JwtException(String message) {
        super(message);
    }
}
