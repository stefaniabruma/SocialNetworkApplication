package ir.map.socialnetworkapp.Domain.Validation;

public interface Validator<T>{
    void validate(T entity) throws ValidationException;
}
