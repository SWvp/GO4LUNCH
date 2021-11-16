package com.kardabel.go4lunch.usecase;


import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.SearchViewRepository;

public class SearchViewUseCase {

    private final SearchViewRepository searchViewRepository;
    private final LocationRepository locationRepository;

    public SearchViewUseCase(SearchViewRepository searchViewRepository,
                             LocationRepository locationRepository
    ){
        this.searchViewRepository = searchViewRepository;
        this.locationRepository = locationRepository;


    }

    public void getSearchViewResults(String keyword){
        String i = keyword;

        String location =
                locationRepository.getLocationLiveData().getValue().getLatitude() +
                ";" +
                locationRepository.getLocationLiveData().getValue().getLongitude();

        //searchViewRepository.getSearchViewListLiveData(location, keyword);

    }


}
