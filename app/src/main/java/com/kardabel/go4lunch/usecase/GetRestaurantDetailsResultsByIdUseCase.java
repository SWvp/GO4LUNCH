package com.kardabel.go4lunch.usecase;

import androidx.lifecycle.LiveData;

import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.repository.RestaurantDetailsResponseRepository;

public class GetRestaurantDetailsResultsByIdUseCase {

    private  final RestaurantDetailsResponseRepository restaurantDetailsResponseRepository;

    public GetRestaurantDetailsResultsByIdUseCase(
            RestaurantDetailsResponseRepository restaurantDetailsResponseRepository){

        this.restaurantDetailsResponseRepository = restaurantDetailsResponseRepository;
    }


    public LiveData<RestaurantDetailsResult> invoke(String placeId) {

        return restaurantDetailsResponseRepository.getRestaurantDetailsLiveData(placeId);

    }
}
