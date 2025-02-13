package org.example.crmsystem.dao.interfaces;

import java.util.List;
import java.util.Optional;

public interface HavingUserName<T> {
    Optional<T> getByUserName(String userName);

    List<T> getWhereUserNameStartsWith(String userName);
}
