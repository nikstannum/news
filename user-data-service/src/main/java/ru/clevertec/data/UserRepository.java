package ru.clevertec.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for interacting with the database.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Method for getting a user by his email
     *
     * @param email user email
     * @return user in container of {@link java.util.Optional}
     */
    Optional<User> findUserByEmail(String email);
}
