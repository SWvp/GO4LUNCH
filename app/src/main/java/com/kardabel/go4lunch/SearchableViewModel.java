package com.kardabel.go4lunch;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.kardabel.go4lunch.repository.UsersSearchRepository;

public class SearchableViewModel extends ViewModel {
    
    private final UsersSearchRepository usersSearchRepository;

    public SearchableViewModel(
            @NonNull UsersSearchRepository usersSearchRepository) {
        this.usersSearchRepository = usersSearchRepository;
        
    }


    public void getSearch(String query) {
        usersSearchRepository.usersSearch(query);

    }
}
