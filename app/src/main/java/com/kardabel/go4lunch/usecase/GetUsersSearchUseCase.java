package com.kardabel.go4lunch.usecase;


import androidx.lifecycle.LiveData;

import com.kardabel.go4lunch.pojo.SearchViewResult;
import com.kardabel.go4lunch.repository.UsersSearchRepository;

public class GetUsersSearchUseCase {

    private UsersSearchRepository usersSearchRepository;

    public GetUsersSearchUseCase(
            UsersSearchRepository usersSearchRepository){

        this.usersSearchRepository = usersSearchRepository;
    }


    public LiveData<SearchViewResult> invoke() {

        // TODO : switchmap

        return usersSearchRepository.getUsersSearchLiveData();

    }


}
