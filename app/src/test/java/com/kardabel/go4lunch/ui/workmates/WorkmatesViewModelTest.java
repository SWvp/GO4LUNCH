package com.kardabel.go4lunch.ui.workmates;


import android.app.Application;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.kardabel.go4lunch.pojo.NearbySearchResults;
import com.kardabel.go4lunch.pojo.RestaurantDetailsResult;
import com.kardabel.go4lunch.usecase.NearbySearchResultsUseCase;
import com.kardabel.go4lunch.usecase.RestaurantDetailsResultsUseCase;

import org.junit.Rule;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

public class WorkmatesViewModelTest {

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private final NearbySearchResultsUseCase nearbySearchResultsUseCase = Mockito.mock(NearbySearchResultsUseCase.class);
    private final RestaurantDetailsResultsUseCase restaurantDetailsResultsUseCase = Mockito.mock(RestaurantDetailsResultsUseCase.class);

    private final Location location = Mockito.mock(Location.class);
    private final Application application = Mockito.mock(Application.class);
    private final Clock clock = Clock.fixed(
            LocalDateTime
                    .of(
                            LocalDate.of(2021, 10, 20),
                            LocalTime.of(10, 0)
                    )
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC
    );

    private final MutableLiveData<NearbySearchResults> nearbySearchResultsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<RestaurantDetailsResult>> restaurantDetailsResultsUseCaseMutableLiveData = new MutableLiveData<>();

    private WorkMatesViewModel workMatesViewModel;
}
