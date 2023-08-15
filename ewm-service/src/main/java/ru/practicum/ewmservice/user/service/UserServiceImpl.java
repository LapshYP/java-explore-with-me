package ru.practicum.ewmservice.user.service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.model.User;
import ru.practicum.ewmservice.user.repository.UserRepoJpa;


import javax.validation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepoJpa userRepoJpa;

    private final ModelMapper mapper = new ModelMapper();

    private void validateUser(User user) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @SneakyThrows
    @Override
    public UserDto create(UserDto userDTO) {
        User user = mapper.map(userDTO, User.class);
        validateUser(user);
        if (user.getName().length()<2 || user.getName().length()>250) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST,"Name field must be >2 and <250");}
        if (user.getEmail().length()<6 || user.getEmail().length()>254) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST,"Email field must be >6 and <64");}
        User savedUser = userRepoJpa.save(user);
        log.debug("Пользователь создан, id = {} , name= {}", user.getId(), user.getName());
        UserDto savedUserDto = mapper.map(savedUser, UserDto.class);
        return savedUserDto;
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        if (ids != null) {  ids =  ids.stream().filter(aLong -> aLong>0).collect(Collectors.toList());}

        List<User> users;
        Pageable pageable = PageRequest.of(from / size, size,Sort.by(Sort.Direction.ASC, "id"));


        if (ids == null || ids.size()==0) {
            users = userRepoJpa.findAll(pageable).toList();
        } else {
            users = userRepoJpa.findAllByIdIn(ids, pageable);
        }
        log.debug("getAll,ids = {}, from = {} , size= {}", ids , from, size);
        return users.stream()
                .map(user -> {
                    return mapper.map(user, UserDto.class);
                })
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    @Transactional
    public UserDto updateUser(UserDto userDTO, Long userId) {
        User user = mapper.map(userDTO, User.class);
        User updatedUser = userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }

        if (user.getEmail() != null) {

            updatedUser.setEmail(user.getEmail());
        }

        updatedUser.setId(userId);
        validateUser(updatedUser);
        userRepoJpa.save(updatedUser);
        log.debug("Пользователь обновлен,  id = {}, name= {} обновлен", user.getId(), user.getName());
        UserDto updatedUserDto = mapper.map(updatedUser, UserDto.class);
        return updatedUserDto;
    }

    @Override
    public UserDto deleteUser(Long userId) {
        User user = userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));
        UserDto userDTO = mapper.map(user, UserDto.class);
        userRepoJpa.deleteById(userId);
        log.debug("Пользователь удален, userId = {} ", userId);
        return userDTO;
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepoJpa.findById(userId).orElseThrow(() -> new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id = '" + userId + "' не найден"));
        UserDto userDTO = mapper.map(user, UserDto.class);
        log.debug("Пользователь просмотрен, userId = {}  ", userId);
        return userDTO;
    }
}