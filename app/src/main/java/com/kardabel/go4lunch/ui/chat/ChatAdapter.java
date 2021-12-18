package com.kardabel.go4lunch.ui.chat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kardabel.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<ChatViewState> chatMessagesList = new ArrayList<>();
    public static final int MESSAGE_TYPE_IN = 1;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    // VIEW HOLDER FOR INCOMING MESSAGE
    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView message, date;

        MessageInViewHolder(final View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.incoming_message_text);
            date = itemView.findViewById(R.id.incoming_message_date);
        }

        void bind(int position) {
            ChatViewState chatViewState = chatMessagesList.get(position);
            message.setText(chatViewState.getChatMessageViewState());
            date.setText(chatViewState.getChatMessageTimeViewState());
        }
    }

    // VIEW HOLDER FOR OUTGOING MESSAGE
    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView message, date;

        MessageOutViewHolder(final View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.outgoing_message_text);
            date = itemView.findViewById(R.id.outgoing_message_date);
        }

        void bind(int position) {
            ChatViewState chatViewState = chatMessagesList.get(position);
            message.setText(chatViewState.getChatMessageViewState());
            date.setText(chatViewState.getChatMessageTimeViewState());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_IN) {
            return new MessageInViewHolder(LayoutInflater
                    .from(context)
                    .inflate(R.layout.item_chat_incoming, parent, false));
        }
        return new MessageOutViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_chat_outgoing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (chatMessagesList.get(position).getChatMessageTypeViewState() == MESSAGE_TYPE_IN) {
            ((MessageInViewHolder) holder).bind(position);
        } else {
            ((MessageOutViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        if (chatMessagesList == null) {
            return 0;
        } else {
            return chatMessagesList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessagesList.get(position).getChatMessageTypeViewState();
    }

    // CHAT MESSAGES LIST FROM ACTIVITY
    @SuppressLint("NotifyDataSetChanged")
    public void setChatMessagesListData(List<ChatViewState> chatMessagesList) {
        this.chatMessagesList = chatMessagesList;
        notifyDataSetChanged();

    }
}
