package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.dto.BankTransferDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.BankTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller Class for managing bank transfer between user and bank account.
 */
@Controller
@Validated
public class BankTransferController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BankTransferController.class);

  @Autowired
  BankTransferService bankTransferService;

  /**
   * Handle HTTP GET request on all bank transfers.
   *
   * @param pageable of the requested page
   * @return HTTP 200 with bank transfers page
   */
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/banktransfers")
  public ResponseEntity<Page<BankTransferDto>> getInfoById(Pageable pageable) {

    LOGGER.info("Request: Get all bank transfers");
    Page<BankTransferDto> bankTransfersDto = bankTransferService.getAll(pageable);

    LOGGER.info("Response: All bank transfers information sent");
    return ResponseEntity.ok(bankTransfersDto);
  }


  /**
   * Handle HTTP GET request on all bank transfers of an user.

   * @param id of the user
   * @return HTTP 200 Response with user's information
   * @throws ResourceNotFoundException when user not found
   */
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
  @GetMapping("/banktransfers/user")
  public ResponseEntity<Page<BankTransferDto>> getInfoById(@RequestParam int id, Pageable pageable)
      throws ResourceNotFoundException {

    LOGGER.info("Request: Get user {} bank transfers", id);
    Page<BankTransferDto> bankTransfersDto = bankTransferService.getFromUser(id, pageable);

    LOGGER.info("Response: User bank transfers sent");
    return ResponseEntity.ok(bankTransfersDto);
  }

}
