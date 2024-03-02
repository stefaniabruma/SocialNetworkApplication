package ir.map.socialnetworkapp.Domain.Validation;

import ir.map.socialnetworkapp.Domain.FriendshipRequest;

public class FriendshipRequestValidator implements Validator<FriendshipRequest> {

    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            throw new ValidationException("Can't have a friend request to the same user!");
    }
}
