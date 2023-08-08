package ru.practicum.ewmservice.user.service;


import ru.practicum.ewmservice.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUserSerivce(UserDto userDTO);

    List<UserDto> getAll();

    UserDto updateUserService(UserDto userDTO, Long userId);

    UserDto deleteUserService(Long userId);

    UserDto getUserSerivece(Long userId);

}
