package com.kardabel.go4lunch.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kardabel.go4lunch.databinding.ChatActivityBinding;
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity;

public class ChatActivity extends AppCompatActivity {

    private static final String WORMATE_ID = "WORKMATE_ID";

    private ChatActivityBinding binding;

    public static Intent navigate(Context context, String workmateId){
        Intent intent = new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(WORMATE_ID, workmateId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
