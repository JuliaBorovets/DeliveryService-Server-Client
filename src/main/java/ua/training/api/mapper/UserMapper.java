package ua.training.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ua.training.api.dto.UserDto;
import ua.training.domain.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToUserDto(User user);

    @Mappings({
            @Mapping(target = "orders", ignore = true),
            @Mapping(target = "receipts", ignore = true),
            @Mapping(target = "cards", ignore = true)
    })
    User userDtoToUser(UserDto userDto);
}
