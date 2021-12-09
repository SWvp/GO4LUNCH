package com.kardabel.go4lunch.usecase;

import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
    public static final String COLLECTION_CHAT = "chat";
    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    public final FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
    public final FirebaseAuth mockFirebaseAuth = Mockito.mock(FirebaseAuth.class);
    public final FirebaseUser mockFirebaseUser = Mockito.mock(FirebaseUser.class);
    public final CollectionReference mockCollection = Mockito.mock(CollectionReference.class);

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
    public void when_Add_Message() {

        addChatMessageToFirestoreUseCase.createChatMessage("message", "1234");



    }


}
