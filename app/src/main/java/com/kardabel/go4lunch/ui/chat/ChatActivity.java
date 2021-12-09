package com.kardabel.go4lunch.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kardabel.go4lunch.databinding.ChatActivityBinding;
import com.kardabel.go4lunch.di.ViewModelFactory;
import com.kardabel.go4lunch.usecase.AddChatMessageToFirestoreUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private static final String WORKMATE_ID = "WORKMATE_ID";
    private static final String WORKMATE_NAME = "WORKMATE_NAME";
    private static final String WORKMATE_PHOTO = "WORKMATE_PHOTO";

    private ChatActivityBinding binding;
    private ChatViewModel chatViewModel;

    public static Intent navigate(
            Context context,
            String workmateId,
            String workmateName,
            String workmatePhoto) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(WORKMATE_ID, workmateId);
        intent.putExtra(WORKMATE_NAME, workmateName);
        intent.putExtra(WORKMATE_PHOTO, workmatePhoto);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<ChatViewState> chatMessageList = new ArrayList<>();
        ChatAdapter adapter = new ChatAdapter(this);

        binding.activityChatRecyclerView.setAdapter(adapter);
        binding.activityChatRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        ChatActivity.this,
                        RecyclerView.VERTICAL,
                        false));

        // CONFIGURE VIEWMODEL
        ViewModelFactory chatViewModelFactory = ViewModelFactory.getInstance();
        chatViewModel =
                new ViewModelProvider(this, chatViewModelFactory).get(ChatViewModel.class);

        // INIT THE VIEW MODEL WITH THE WORKMATE ID PASSED IN INTENT
        // (WE NEED CURRENT USER TOO BUT IT WILL BE LATER, IN THE USECASE)
        Intent intent = getIntent();
        chatViewModel.init(intent.getStringExtra(WORKMATE_ID));

        // FEED THE VIEW WITH MATE INFORMATION
        binding.mateName.setText(intent.getStringExtra(WORKMATE_NAME));
        Glide.with(binding.workmatePhoto.getContext())
                .load(intent.getStringExtra(WORKMATE_PHOTO))
                .circleCrop()
                .into(binding.workmatePhoto);

        chatViewModel.getChatMessages().observe(this, new Observer<List<ChatViewState>>() {
            @Override
            public void onChanged(List<ChatViewState> chatViewStatesList) {
                adapter.setChatMessagesListData(chatViewStatesList);

                // MOVE TO THE LATEST MESSAGE
                binding.activityChatRecyclerView.scrollToPosition(chatViewStatesList.size() - 1);

            }
        });

                binding.activityChatSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Objects.requireNonNull(binding.chatMessageEditText.getText()).toString().isEmpty()) {
                            String message = binding.chatMessageEditText.getText().toString();
                            chatViewModel.createChatMessage(message, intent.getStringExtra(WORKMATE_ID));
                            binding.chatMessageEditText.getText().clear();
                            hideSoftKeyboard(ChatActivity.this);

                        } else {
                            Toast.makeText(
                                    ChatActivity.this,
                                    "Type your text first !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null) return;
        if (activity.getCurrentFocus() == null) return;

        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

    }
}
