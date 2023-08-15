package ru.practicum.ewmservice.user.service;


import ru.practicum.ewmservice.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDTO);

    List<UserDto> getAll(List<Long> ids,  int from, int size);

    UserDto updateUser(UserDto userDTO, Long userId);

    UserDto deleteUser(Long userId);

    UserDto getUser(Long userId);

}
