package ru.clevertec.service.dto;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * the essence of the web interface contained in the comments or news as the author of these items
 */
@Getter
@Setter
@EqualsAndHashCode
public class AuthorReadDto implements Serializable {
    private String firstName;
    private String lastName;
}
