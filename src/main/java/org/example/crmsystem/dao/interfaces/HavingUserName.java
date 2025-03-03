package org.example.crmsystem.dao.interfaces;

import java.util.List;
import java.util.Optional;

public interface HavingUserName<T> {
    Optional<T> getByUsername(String userName);

    List<T> getWhereUsernameStartsWith(String userName);
}
