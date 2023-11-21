package scc.utils;

public class Login {
    String id;
    String password;

    public Login() {

    }

    public Login(String username, String password) {
        this.id = username;
        this.password = password;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String username) {
        this.id = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
