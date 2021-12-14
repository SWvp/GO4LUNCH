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

public class AddChatMessageToFireStoreUseCaseTest {

    public static final String USER_ID = "2345";
    public static final String MESSAGE = "message";
    public static final String WORKMATE_ID = "1234";
    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    public final FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
    public final FirebaseAuth mockFirebaseAuth = Mockito.mock(FirebaseAuth.class);
    public final FirebaseUser mockFirebaseUser = Mockito.mock(FirebaseUser.class);

    private final Clock clock = Clock.fixed(
            LocalDateTime
                    .of(
                            LocalDate.of(2021, 10, 20),
                            LocalTime.of(10, 0)
                    )
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );

    private AddChatMessageToFirestoreUseCase addChatMessageToFirestoreUseCase;

    @Before
    public void setUp() {

        Mockito.doReturn(mockFirebaseUser)
                .when(mockFirebaseAuth)
                .getCurrentUser();

        Mockito.doReturn(USER_ID)
                .when(mockFirebaseUser)
                .getUid();


        addChatMessageToFirestoreUseCase =
                new AddChatMessageToFirestoreUseCase(
                        mockFirestore,
                        mockFirebaseAuth,
                        clock);

    }

    @Test
    public void when_Create_Chat_Message_Is_Called() {

        addChatMessageToFirestoreUseCase = mock(AddChatMessageToFirestoreUseCase.class);

        doNothing().when(addChatMessageToFirestoreUseCase).createChatMessage(
                isA(String.class),
                isA(String.class));
        addChatMessageToFirestoreUseCase.createChatMessage(
                MESSAGE,
                WORKMATE_ID);
        verify(addChatMessageToFirestoreUseCase, times(1)).createChatMessage(
                MESSAGE,
                WORKMATE_ID);

    }
}
