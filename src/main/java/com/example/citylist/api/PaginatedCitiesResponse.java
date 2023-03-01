package com.example.citylist.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedCitiesResponse {

    private int totalNumberOfCities;
    private int totalPages;
    private List<CityDto> cityDtoList;
}
