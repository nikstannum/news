package ru.clevertec.data.converter;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import ru.clevertec.data.User.UserRole;
import ru.clevertec.logger.LogInvocation;

import static ru.clevertec.data.User.UserRole.ADMIN;
import static ru.clevertec.data.User.UserRole.JOURNALIST;
import static ru.clevertec.data.User.UserRole.SUBSCRIBER;

@Component
public class UserRoleConverter implements AttributeConverter<UserRole, Integer> {
    @Override
    @LogInvocation
    public Integer convertToDatabaseColumn(UserRole attribute) {
        switch (attribute) {
            case ADMIN -> {
                return 1;
            }
            case JOURNALIST -> {
                return 2;
            }
            case SUBSCRIBER -> {
                return 3;
            }
            default -> throw new IllegalArgumentException(attribute + " not supported");
        }
    }

    @Override
    @LogInvocation
    public UserRole convertToEntityAttribute(Integer dbData) {
        switch (dbData) {
            case 1 -> {
                return ADMIN;
            }
            case 2 -> {

                return JOURNALIST;
            }
            case 3 -> {
                return SUBSCRIBER;
            }
            default -> throw new IllegalArgumentException(dbData + " not supported");
        }
    }
}
