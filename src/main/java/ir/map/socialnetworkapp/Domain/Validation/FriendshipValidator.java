package ir.map.socialnetworkapp.Domain.Validation;


import ir.map.socialnetworkapp.Domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {

        if(entity.getId().getRight().equals(entity.getId().getLeft()))
            throw new ValidationException("Can't have a friendship to the same user!\n");

    }
}
