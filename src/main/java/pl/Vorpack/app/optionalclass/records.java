package pl.Vorpack.app.optionalclass;

/**
 * Created by Paweł on 2018-02-22.
 */
public class records {

    private long user_id;

    private String login;

    private String password;

    private String admin;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public records() {
    }

    public records(long user_id, String login, String password, String admin) {
        this.user_id = user_id;
        this.login = login;
        this.password = password;
        this.admin = admin;
    }
}
