package com.example.citylist.api;

import com.example.citylist.global.CityException;
import com.example.citylist.util.CsvParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CityService {

    private final CsvParserService csvParserService;
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

    public CityDto getCity(Long id) {
        Optional<City> optionalCity = cityRepository.findById(id);
        if (optionalCity.isPresent()) {
            CityDto cityDto = new CityDto();
            modelMapper.map(optionalCity.get(), cityDto);
            return cityDto;
        } else {
            log.error("Error occurred during getting city by the given id: " + id);
            throw new CityException("City not found with the given id: " + id);
        }
    }

    public PaginatedCitiesResponse getPaginatedListOfCities(String name, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<City> cityList = cityRepository.findByNameContainsIgnoreCase(name, pageRequest);

        if (cityList.hasContent()) {
            List<CityDto> cityDtoList = cityList.getContent().stream().map(city -> {
                CityDto destination = new CityDto();
                modelMapper.map(city, destination);
                return destination;
            }).toList();
            PaginatedCitiesResponse paginatedCitiesResponse = new PaginatedCitiesResponse();
            paginatedCitiesResponse.setCityDtoList(cityDtoList);
            paginatedCitiesResponse.setTotalNumberOfCities(cityDtoList.size());
            paginatedCitiesResponse.setTotalPages(cityList.getTotalPages());
            return paginatedCitiesResponse;
        } else {
            log.error("Error occurred during getting paginated list of cities: Not found any cities.");
            throw new CityException("Not found any cities.");
        }
    }

    public CityDto updateCity(Long id, CityRequestBody cityRequestBody) {
        boolean hasName = StringUtils.isNotBlank(cityRequestBody.getName());
        boolean hasPhotoUrl = StringUtils.isNotBlank(cityRequestBody.getPhotoUrl());
        if (hasName || hasPhotoUrl) {
            Optional<City> optionalCity = cityRepository.findById(id);
            return optionalCity.map(
                            city ->
                                    updateAndSaveCity(cityRequestBody, hasName, hasPhotoUrl, city))
                    .orElseGet(() ->
                            createNewCity(cityRequestBody, hasName, hasPhotoUrl));
        } else {
            log.error("Error occurred during updating city: " + id);
            throw new CityException("Cannot create or update city with empty name or photoUrl.");
        }
    }

    private CityDto createNewCity(CityRequestBody cityRequestBody, boolean hasNewName, boolean hasNewPhotoUrl) {
        boolean cityAlreadyExistWithGivenName = cityRepository.findByNameContainsIgnoreCase(cityRequestBody.getName()).isEmpty();
        if (cityAlreadyExistWithGivenName) {
            return updateAndSaveCity(cityRequestBody, hasNewName, hasNewPhotoUrl, new City());
        } else {
            log.error("Error occurred during creating new city");
            throw new CityException("Cannot create new city, because a city already exists with this name:" + cityRequestBody.getName());
        }
    }

    private CityDto updateAndSaveCity(CityRequestBody cityRequestBody, boolean hasNewName, boolean hasNewPhotoUrl, City city) {
        if (hasNewName)
            city.setName(cityRequestBody.getName());
        if (hasNewPhotoUrl)
            city.setPhotoUrl(cityRequestBody.getPhotoUrl());
        cityRepository.save(city);
        CityDto cityDto = new CityDto();
        modelMapper.map(city, cityDto);
        return cityDto;
    }
}