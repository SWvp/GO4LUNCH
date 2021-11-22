package com.kardabel.go4lunch.usecase;


import com.kardabel.go4lunch.repository.LocationRepository;
import com.kardabel.go4lunch.repository.AutocompleteRepository;

public class SearchViewUseCase {

    private final AutocompleteRepository mAutocompleteRepository;
    private final LocationRepository locationRepository;

    public SearchViewUseCase(AutocompleteRepository autocompleteRepository,
                             LocationRepository locationRepository
    ){
        this.mAutocompleteRepository = autocompleteRepository;
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
