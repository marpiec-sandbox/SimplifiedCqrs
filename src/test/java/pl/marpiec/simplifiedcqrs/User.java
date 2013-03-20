package pl.marpiec.simplifiedcqrs;

/**
 *
 */
public class User extends Aggregate {

    private String name;
    private String password;
    private String email;

    @Override
    public Aggregate copy() {
        User aggregate = new User();
        aggregate.id = this.id;
        aggregate.version = this.version;
        aggregate.name = this.name;
        aggregate.password = this.password;
        aggregate.email = this.email;
        return aggregate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
