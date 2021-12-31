package com.openclassrooms.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserSubscriptionDto;
import com.openclassrooms.paymybuddy.exception.EmailAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.service.UserService;
import com.openclassrooms.paymybuddy.utils.JsonParser;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Captor
  ArgumentCaptor<UserSubscriptionDto> subscriptionCaptor;

  private UserInfoDto userInfoDto;

  @BeforeEach
  void setUp() {
    userInfoDto = new UserInfoDto(1, "test","test","test@mail.com", BigDecimal.ZERO);
  }

  @Test
  void getInfoByIdTest() throws Exception {
    // GIVEN
    when(userService.getInfoById(anyInt())).thenReturn(userInfoDto);

    // WHEN
    mockMvc.perform(get("/users/1"))


        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)));
    verify(userService, times(1)).getInfoById(1);
  }

  @Test
  void getInfoByIdWhenNotFoundTest() throws Exception {
    // GIVEN
    when(userService.getInfoById(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(get("/users/2"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(userService, times(1)).getInfoById(2);
  }

  @Test
  void postSubscriptionTest() throws Exception {
    // GIVEN
    UserSubscriptionDto subscriptionDto = new UserSubscriptionDto("test","test", "test@mail.com","12345678");
    when(userService.subscribe(any(UserSubscriptionDto.class))).thenReturn(userInfoDto);

    // WHEN
    mockMvc.perform(post("/subscribe")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(subscriptionDto)))

        // THEN
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)));
    verify(userService, times(1)).subscribe(subscriptionCaptor.capture());
    assertThat(subscriptionCaptor.getValue()).usingRecursiveComparison().isEqualTo(subscriptionDto);

  }

  @Test
  void postSubscriptionWithAlreadyUsedEmailTest() throws Exception {
    // GIVEN
    UserSubscriptionDto subscriptionDto = new UserSubscriptionDto("test","test", "test@mail.com","12345678");
    when(userService.subscribe(any(UserSubscriptionDto.class))).thenThrow(
        new EmailAlreadyExistsException("This email is already used"));

    // WHEN
    mockMvc.perform(post("/subscribe")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(subscriptionDto)))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("This email is already used")));
    verify(userService, times(1)).subscribe(subscriptionCaptor.capture());
    assertThat(subscriptionCaptor.getValue()).usingRecursiveComparison().isEqualTo(subscriptionDto);
  }

  @Test
  void postInvalidSubscriptionTest() throws Exception {
    // GIVEN
    UserSubscriptionDto invalidSubscriptionDto = new UserSubscriptionDto("","test", "testmail.com","1234");

    // WHEN
    mockMvc.perform(post("/subscribe")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(invalidSubscriptionDto)))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.firstname", is("Firstname is mandatory")))
        .andExpect(jsonPath("$.email", is("Email should be a valid email address")))
        .andExpect(jsonPath("$.password", is("Password should have at least 8 characters")));
    verify(userService, times(0)).subscribe(any(UserSubscriptionDto.class));
  }
}
