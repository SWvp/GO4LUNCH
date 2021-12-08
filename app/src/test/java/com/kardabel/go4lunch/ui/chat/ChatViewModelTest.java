package com.kardabel.go4lunch.ui.chat;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.model.ChatMessageModel;
import com.kardabel.go4lunch.repository.ChatMessageRepository;
import com.kardabel.go4lunch.testutil.LiveDataTestUtils;
import com.kardabel.go4lunch.usecase.AddChatMessageToFirestoreUseCase;
import com.kardabel.go4lunch.usecase.GetCurrentUserIdUseCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModelTest {

    private final String workmateId = "workmate_Id";
    private final String currentUserId = "current_User_Id";

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private final ChatMessageRepository chatMessageRepository =
            Mockito.mock(ChatMessageRepository.class);
    private final GetCurrentUserIdUseCase getCurrentUserIdUseCase =
            Mockito.mock(GetCurrentUserIdUseCase.class);
    private final AddChatMessageToFirestoreUseCase addChatMessageToFirestoreUseCase =
            Mockito.mock(AddChatMessageToFirestoreUseCase.class);

    private final MutableLiveData<List<ChatMessageModel>>  chatMessagesLiveData =
            new MutableLiveData<>();

    private ChatViewModel chatViewModel;


    @Before
    public void setUp(){

        // RETURNS OF MOCKED CLASS
        Mockito.doReturn(chatMessagesLiveData)
                .when(chatMessageRepository)
                .getChatMessages(workmateId);
        Mockito.doReturn(currentUserId)
                .when(getCurrentUserIdUseCase)
                .invoke();

        // SET LIVEDATA VALUES
        chatMessagesLiveData.setValue(getDefaultChatMessages());

        chatViewModel = new ChatViewModel(
                chatMessageRepository,
                getCurrentUserIdUseCase,
                addChatMessageToFirestoreUseCase
        );
    }

    @Test
    public void retrieveChatMessages(){
        // WHEN
        chatViewModel.init(workmateId);
        LiveDataTestUtils.observeForTesting(chatViewModel.getChatMessages(), chatMessages -> {
            // THEN
            assertEquals(ChatViewModelTest.this.getDefaultChatMessagesViewState(), chatMessages);

        });
    }

    // region IN

    // VAL FOR TESTING
    String messageFromMe1 = "message 1 from me";
    String date = "date";
    Long timestamp = 1L;

    String messageFromYou1 = "message 1 from you";
    String date2 = "date2";
    Long timestamp2 = 2L;

    String messageFromYou2 = "message 2 from you";
    String date3 = "date3";
    Long timestamp3 = 3L;

    String messageFromMe2 = "message 2 from me";
    String date4 = "date4";
    Long timestamp4 = 4L;

    String messageFromYou3 = "message 3 from you";
    String date5 = "date5";
    Long timestamp5 = 5L;

    private List<ChatMessageModel> getDefaultChatMessages() {
        List<ChatMessageModel> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessageModel(
                messageFromMe1,
                currentUserId,
                date,
                timestamp
        ));
        chatMessages.add(new ChatMessageModel(
                messageFromYou1,
                workmateId,
                date2,
                timestamp2

        ));
        chatMessages.add(new ChatMessageModel(
                messageFromYou2,
                workmateId,
                date3,
                timestamp3

        ));
        chatMessages.add(new ChatMessageModel(
                messageFromMe2,
                currentUserId,
                date4,
                timestamp4

        ));
        chatMessages.add(new ChatMessageModel(
                messageFromYou3,
                workmateId,
                date5,
                timestamp5

        ));
        return chatMessages;

    }

    // endregion

    // region Out

    private List<ChatViewState> getDefaultChatMessagesViewState() {
        List<ChatViewState> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatViewState(
                messageFromMe1,
                2,
                date,
                timestamp
        ));
        chatMessages.add(new ChatViewState(
                messageFromYou1,
                1,
                date2,
                timestamp2

        ));
        chatMessages.add(new ChatViewState(
                messageFromYou2,
                1,
                date3,
                timestamp3

        ));
        chatMessages.add(new ChatViewState(
                messageFromMe2,
                2,
                date4,
                timestamp4

        ));
        chatMessages.add(new ChatViewState(
                messageFromYou3,
                1,
                date5,
                timestamp5

        ));
        return chatMessages;

    }

    // endregion
}
