package pl.Vorpack.app.Service.ServiceImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Constans.UserColumnConstans;
import pl.Vorpack.app.DatabaseAccess.UsersAccess;
import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.Dto.UsersDTO;
import pl.Vorpack.app.Service.UserService;

import java.util.List;

import static pl.Vorpack.app.Constans.UserColumnConstans.ALL;

public class UserServiceImpl implements UserService {

    private UsersAccess usersAccess = new UsersAccess();

    public UserServiceImpl() {
    }

    @Override
    public User findByLogin(String login){
        List<User> users = usersAccess.findByLogin(login);
        return !users.isEmpty() ? users.get(0) : null;
    }

    @Override
    public FilteredList<User> findAll(){
        List<User> users = usersAccess.findAll();
        users.removeIf(u -> u.getLogin().equals("Admin"));
        ObservableList<User> data = FXCollections.observableArrayList(users);
        return new FilteredList<>(data, p -> true);
    }

    @Override
    public void create(User user){
        usersAccess.create(user);
    }

    @Override
    public void update(User user){
        usersAccess.update(user);
    }

    @Override
    public void delete(User user){
        usersAccess.delete(user);
    }

    @Override
    public void filter(String filter, String searchedText, FilteredList<UsersDTO> users) {
        if (filter == null || filter.equals(ALL))
            filterByEveryColumn(searchedText, users);
        else
            filterByOneColumn(filter, searchedText, users);
    }

    private void filterByEveryColumn(String searchedText, FilteredList<UsersDTO> users) {
        users.setPredicate(obj -> {
            if (searchedText == null || searchedText.isEmpty())
                return true;
            String lowerCaseValue = searchedText.toLowerCase();
            if (String.valueOf(obj.getUser_id()).toLowerCase().contains(lowerCaseValue))
                return true;
            if (String.valueOf(obj.getLogin()).toLowerCase().contains(lowerCaseValue))
                return true;
            if (String.valueOf(obj.getPassword()).toLowerCase().contains(lowerCaseValue))
                return true;
            else return String.valueOf(obj.getAdmin()).toLowerCase().contains(lowerCaseValue);
        });
    }

    private void filterByOneColumn(String filter, String searchedText, FilteredList<UsersDTO> clients) {
        clients.setPredicate(obj -> {
            if (searchedText == null || searchedText.isEmpty())
                return true;
            String lowerCaseValue = getNonNullLowerCaseValue(searchedText);
            String filterValue = getFilter(filter, obj);
            return filterValue.contains(lowerCaseValue);
        });
    }

    private String getNonNullLowerCaseValue(String string) {
        if (string == null)
            return "";

        return string.toLowerCase();
    }

    private String getFilter(String column, UsersDTO obj) {
        switch (column) {
            case UserColumnConstans.ID:
                return String.valueOf(obj.getUser_id()).toLowerCase();
            case UserColumnConstans.LOGIN:
                return String.valueOf(obj.getLogin()).toLowerCase();
            case UserColumnConstans.PASSWORD:
                return String.valueOf(obj.getPassword()).toLowerCase();
            case UserColumnConstans.ADMINISTRATOR:
                return String.valueOf(obj.getAdmin()).toLowerCase();
            default:
                return "";
        }
    }
}
