package dk.race.racekatteklubben.domain.exception;

public class PetAlreadyAttendingException extends RuntimeException {
    public PetAlreadyAttendingException(String message) {
        super(message);
    }
}
