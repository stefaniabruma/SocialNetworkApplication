package ir.map.socialnetworkapp.Domain.Validation;

import ir.map.socialnetworkapp.Domain.Message;

public class MessageValidator implements Validator<Message> {

    @Override
    public void validate(Message entity) throws ValidationException {

        String errors = "";

        if(entity.getFrom() == null)
            errors += "From must not be null!\n";
        if(entity.getTo().isEmpty())
            errors += "Message must have at least one destination!\n";
        if(entity.getText().isEmpty())
            errors += "Text must not be null!\n";
        if(entity.getDate() == null)
            errors += "Date must not be null!\n";

        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
