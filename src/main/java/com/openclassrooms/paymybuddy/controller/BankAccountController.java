package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.BankAccountService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller Class for managing user bank account.
 */
@Controller
@Validated
public class BankAccountController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountController.class);

  @Autowired
  BankAccountService bankAccountService;

  /**
   * Handle HTTP GET request on user's bank accounts by id.
   *
   * @param id of the user
   * @param pageable of the requested page
   * @return HTTP 200 Response with bank accounts page
   * @throws ResourceNotFoundException if user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/users/{id}/bankaccounts")
  public ResponseEntity<Page<BankAccountDto>> getAllfromUser(@PathVariable int id,
                                                             Pageable pageable)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} bank accounts", id);
    Page<BankAccountDto> bankAccounts = bankAccountService.getAllFromUser(id, pageable);

    LOGGER.info("Response: Page of user bank accounts sent");
    return ResponseEntity.ok(bankAccounts);
  }

  /**
   * Handle HTTP POST request on user bank accounts.
   *
   * @param id of user
   * @param bankAccount to add
   * @return HTTP 201
   * @throws ResourceNotFoundException if user not found
   * @throws ResourceAlreadyExistsException if bank account already exists
   */
  @PreAuthorize("#id == authentication.principal.userId")
  @PostMapping("/users/{id}/bankaccounts")
  public ResponseEntity<BankAccountDto> addToUser(@PathVariable int id, @Valid @RequestBody
      BankAccountDto bankAccount)
      throws ResourceNotFoundException, ResourceAlreadyExistsException {

    LOGGER.info("Request: Add user {} new bank account", id);
    BankAccountDto bankAccountAdded = bankAccountService.addToUser(id, bankAccount);

    LOGGER.info("Response: User bank account added");
    return ResponseEntity.status(HttpStatus.CREATED).body(bankAccountAdded);

  }

  /**
   * Handle HTTP DELETE request on a user bank account by id.
   *
   * @param id of user
   * @param accountId of the account to delete
   * @return HTTP 204
   * @throws ResourceNotFoundException if user or account not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @DeleteMapping("/users/{id}/bankaccounts/{accountId}")
  public ResponseEntity<Void> removeFromUser(@PathVariable int id, @PathVariable int accountId)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Delete user {} bank account {}", id, accountId);
    bankAccountService.removeFromUser(id, accountId);

    LOGGER.info("Response: user bank account deleted");
    return ResponseEntity.noContent().build();

  }

}
