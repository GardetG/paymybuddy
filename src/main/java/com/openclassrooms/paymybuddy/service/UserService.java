package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserRegistrationDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Interface for managing users.
 */
@Service
public interface UserService {

  Page<UserInfoDto> getAll(Pageable pageable);

  UserInfoDto getById(int id) throws ResourceNotFoundException;

  UserInfoDto register(UserRegistrationDto user) throws ResourceAlreadyExistsException;

  UserInfoDto update(UserInfoDto user) throws ResourceNotFoundException,
      ResourceAlreadyExistsException;

  void deleteById(int id) throws ResourceNotFoundException;

}
