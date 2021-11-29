package com.kardabel.go4lunch.ui.chat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kardabel.go4lunch.R;
import com.kardabel.go4lunch.model.ChatMessageModel;
import com.kardabel.go4lunch.ui.restaurants.RestaurantsViewState;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<ChatViewState> chatMessagesList;
    public static final int MESSAGE_TYPE_IN = 1;
   // public static final int MESSAGE_TYPE_OUT = 2;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    // VIEW HOLDER FOR INCOMING MESSAGE
    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView message,date;

        MessageInViewHolder(final View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.incoming_message_text);
            date = itemView.findViewById(R.id.incoming_message_date);
        }
        void bind(int position) {
            ChatViewState chatViewState = chatMessagesList.get(position);
            message.setText(chatViewState.getChatMessageViewState());
            date.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(chatViewState.getChatMessageTimeViewState()));
        }
    }

    // VIEW HOLDER FOR OUTGOING MESSAGE
    private class MessageOutViewHolder extends RecyclerView.ViewHolder {

        TextView message,date;
        MessageOutViewHolder(final View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.outgoing_message_text);
            date = itemView.findViewById(R.id.outgoing_message_date);
        }
        void bind(int position) {
            ChatViewState chatViewState = chatMessagesList.get(position);
            message.setText(chatViewState.getChatMessageViewState());
            date.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(chatViewState.getChatMessageTimeViewState()));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_IN) {
            return new MessageInViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_incoming, parent, false));
        }
        return new MessageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chat_outgoing, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (chatMessagesList.get(position).getChatMessageTypeViewState() == MESSAGE_TYPE_IN) {
            ((MessageInViewHolder) holder).bind(position);
        } else {
            ((MessageOutViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessagesList.size();
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
