package pl.Vorpack.app.Service;

import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.Dto.UsersDTO;

public interface UserService {
    User findByLogin(String login);

    FilteredList<User> findAll();

    void create(User user);

    void update(User user);

    void delete(User user);

    void filter(String searchedText, FilteredList<UsersDTO> users);
}
