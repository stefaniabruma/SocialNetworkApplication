package ir.map.socialnetworkapp.Domain.Validation;


import ir.map.socialnetworkapp.Domain.User;

public class UserValidator implements Validator<User>{

    @Override
    public void validate(User entity) throws ValidationException {

        String errMsg = "";

        if(entity.getId() == 0)
            errMsg += "Invalid ID!\n";

        if(entity.getFirstName().isEmpty())
            errMsg += "Invalid First Name!\n";

        if(entity.getLastName().isEmpty())
            errMsg += "Invalid Last Name!\n";

        if(!errMsg.isEmpty())
            throw new ValidationException(errMsg);

    }
}
