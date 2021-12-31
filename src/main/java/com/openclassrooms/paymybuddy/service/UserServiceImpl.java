package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserSubscriptionDto;
import com.openclassrooms.paymybuddy.exception.EmailAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.UserMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class for users.
 */
@Service
public class UserServiceImpl implements UserService {

  private static final String USER_NOT_FOUND = "This user is not found";
  private static final String EMAIL_ALREADY_EXIST = "This email is already used";

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserInfoDto getInfoById(int id) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }

    return UserMapper.toInfoDto(user.get());
  }

  @Override
  public UserInfoDto subscribe(UserSubscriptionDto user) throws EmailAlreadyExistsException {
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXIST);
    }

    User userToCreate = UserMapper.toModel(user);

    return UserMapper.toInfoDto(userRepository.save(userToCreate));
  }

  @Override
  public UserInfoDto update(UserInfoDto user) throws ResourceNotFoundException,
      EmailAlreadyExistsException {
    Optional<User> existingUser = userRepository.findById(user.getUserId());
    if (existingUser.isEmpty()) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }
    if (!existingUser.get().getEmail().equals(user.getEmail())
        && userRepository.existsByEmail(user.getEmail())) {
      throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXIST);
    }

    User userToUpdate = existingUser.get();
    userToUpdate.setFirstname(user.getFirstname());
    userToUpdate.setLastname(user.getLastname());
    userToUpdate.setEmail(user.getEmail());

    return UserMapper.toInfoDto(userRepository.save(userToUpdate));
  }

  @Override
  public void deleteById(int id) throws ResourceNotFoundException {

    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException(USER_NOT_FOUND);
    }
    userRepository.deleteById(id);

  }

}
