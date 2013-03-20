package pl.marpiec.simplifiedcqrs.event;

import pl.marpiec.simplifiedcqrs.Aggregate;
import pl.marpiec.simplifiedcqrs.Event;
import pl.marpiec.simplifiedcqrs.User;

public class CreateUserEvent implements Event<User>{

    private final String name;
    private final String email;
    private final String password;

    public CreateUserEvent(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    @Override
    public void applyEvent(Aggregate aggregate) {
        User user = (User) aggregate;
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
    }

    @Override
    public Class<User> getAggregateClass() {
        return User.class;
    }
}
