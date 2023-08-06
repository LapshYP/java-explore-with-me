package ru.practicum.ewmservice.user.service;


import ru.practicum.ewmservice.user.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUserSerivce(UserDTO userDTO);

    List<UserDTO> getAll();

    UserDTO updateUserService(UserDTO userDTO, Long userId);

    UserDTO deleteUserService(Long userId);

    UserDTO getUserSerivece(Long userId);

}
