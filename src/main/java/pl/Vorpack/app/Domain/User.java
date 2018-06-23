package pl.Vorpack.app.Domain;

import javax.persistence.*;

/**
 * Created by Paweł on 2018-02-02.
 */

@Entity
@Table(name = "Uzytkownicy")
public class User {

    private long user_id;
    private String login;
    private String password;
    private boolean admin;

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public User(){

    }

    public User(String a, String b, boolean c){

        login = a;
        password = b;
        admin = c;
    }
}
