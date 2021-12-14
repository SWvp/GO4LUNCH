package com.kardabel.go4lunch.usecase;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class ClickOnChoseRestaurantButtonUseCasTest {

    public static final String USER_ID = "2345";
    public static final String RESTAURANT_ID = "1234";
    public static final String RESTAURANT_NAME = "Galopin";
    public static final String RESTAURANT_ADDRESS = "5 de la corniche";

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    public final FirebaseFirestore mockFirestore = mock(FirebaseFirestore.class);
    public final FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
    public final FirebaseUser mockFirebaseUser = mock(FirebaseUser.class);

    private final Clock clock = Clock.fixed(
            LocalDateTime
                    .of(
                            LocalDate.of(2021, 10, 20),
                            LocalTime.of(10, 0)
                    )
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );

    private ClickOnChoseRestaurantButtonUseCase clickOnChoseRestaurantButtonUseCase;

    @Before
    public void setUp() {

        Mockito.doReturn(mockFirebaseUser)
                .when(mockFirebaseAuth)
                .getCurrentUser();

        Mockito.doReturn(USER_ID)
                .when(mockFirebaseUser)
                .getUid();

        clickOnChoseRestaurantButtonUseCase = new ClickOnChoseRestaurantButtonUseCase(
                mockFirestore,
                mockFirebaseAuth,
                clock
        );

    }

    @Test
    public void when_User_Click_On_Chose_Button() {
        clickOnChoseRestaurantButtonUseCase = mock(ClickOnChoseRestaurantButtonUseCase.class);

        doNothing().when(clickOnChoseRestaurantButtonUseCase).onRestaurantSelectedClick(
                isA(String.class),
                isA(String.class),
                isA(String.class));
        clickOnChoseRestaurantButtonUseCase.onRestaurantSelectedClick(
                RESTAURANT_ID,
                RESTAURANT_NAME,
                RESTAURANT_ADDRESS);
        verify(clickOnChoseRestaurantButtonUseCase, times(1)).onRestaurantSelectedClick(
                RESTAURANT_ID,
                RESTAURANT_NAME,
                RESTAURANT_ADDRESS);


    }

}
