package pl.marpiec.simplifiedcqrs.event;

import pl.marpiec.simplifiedcqrs.Aggregate;
import pl.marpiec.simplifiedcqrs.Event;
import pl.marpiec.simplifiedcqrs.User;

/**
 *
 */
public class ChangePasswordEvent implements Event<User> {

    private final String newPassword;

    public ChangePasswordEvent(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public void applyEvent(Aggregate aggregate) {
        User user = (User) aggregate;
        user.setPassword(newPassword);
    }

    @Override
    public Class<User> getAggregateClass() {
        return User.class;
    }
}
