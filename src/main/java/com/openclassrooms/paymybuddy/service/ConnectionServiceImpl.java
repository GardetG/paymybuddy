package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.constant.ErrorMessage;
import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.utils.ConnectionMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class for managing user's connections with other users.
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionServiceImpl.class);

  @Autowired
  UserRepository userRepository;

  @Override
  public List<ConnectionDto> getAllFromUser(int userId) throws ResourceNotFoundException {
    User user = getUserById(userId);
    return user.getConnections().stream()
        .map(ConnectionMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public ConnectionDto addToUser(int userId, ConnectionDto connection)
      throws ResourceNotFoundException, ResourceAlreadyExistsException,
      ForbiddenOperationException {

    User user = getUserById(userId);
    if (user.getEmail().equals(connection.getEmail())) {
      LOGGER.error("The user can't add himself as connection");
      throw new ForbiddenOperationException("The user can't add himself as connection");
    }
    User connectionToAdd = getUserByEmail(connection.getEmail());

    user.addConnection(connectionToAdd);
    userRepository.save(user);

    return ConnectionMapper.toDto(connectionToAdd);
  }

  @Override
  public void removeFromUser(int userId, int id) throws ResourceNotFoundException {
    User user = getUserById(userId);
    User connectionToDelete = user.getConnections().stream()
        .filter(c -> c.getUserId() == id)
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("This connection is not found");
          return new ResourceNotFoundException("This connection is not found");
        });

    user.removeConnection(connectionToDelete);
    userRepository.save(user);
  }

  private User getUserById(int userId) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      LOGGER.error(ErrorMessage.USER_NOT_FOUND + ": {}", userId);
      throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    return user.get();
  }

  private User getUserByEmail(String email) throws ResourceNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      LOGGER.error(ErrorMessage.USER_NOT_FOUND + ": {}", email);
      throw new ResourceNotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    return user.get();
  }
}