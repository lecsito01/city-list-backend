package com.example.citylist.api;

import com.example.citylist.global.CityException;
import com.example.citylist.util.CsvParserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {CityService.class})
@ExtendWith(SpringExtension.class)
class CityServiceTest {
    public static final String PARIS = "Paris";
    public static final String TOKYO = "Tokyo";
    public static final String HOUSTON = "Houston";
    public static final String TO_AS_SEARCH_INPUT = "To";
    public static final long TEST_ID_31 = 31L;
    @Autowired
    private CityService cityService;
    @MockBean
    private CityRepository cityRepository;

    @MockBean
    private CsvParserService csvParserService;

    @SpyBean
    private ModelMapper modelMapper;

    @Test
    void givenParisCity_whenGetParisFromDb_thenVerifyParisCityResult() {
        //given
        City city = new City();
        city.setId(TEST_ID_31);
        city.setName(PARIS);
        Optional<City> optionalCity = Optional.of(city);

        CityDto cityDto = new CityDto();
        cityDto.setId(TEST_ID_31);
        cityDto.setName(PARIS);

        when(cityRepository.findById(TEST_ID_31)).thenReturn(optionalCity);

        //when
        CityDto cityDtoResult = cityService.getCity(TEST_ID_31);

        //then
        assertEquals(city.getId(), cityDtoResult.getId());
        assertEquals(city.getName(), cityDtoResult.getName());
        verify(cityRepository).findById(TEST_ID_31);
        verify(modelMapper).map(any(City.class), any(CityDto.class));
    }

    @Test
    void givenEmptyResult_whenCallGetCity_thenAssertEntityNotFoundExceptionThrown() {
        //given
        when(cityRepository.findById(TEST_ID_31)).thenReturn(Optional.empty());
        doNothing().when(modelMapper).map(any(City.class), any(CityDto.class));

        //when + then
        assertThrows(CityException.class, () -> cityService.getCity(TEST_ID_31));
        verify(cityRepository).findById(TEST_ID_31);
    }

    @Test
    void givenNoNameNorPhotoUrl_whenUpdateCity_thenCheckCityExceptionThrown() {
        //given nothing -> empty CityRequestBody

        //when
        assertThrows(CityException.class, () -> cityService.updateCity(123L, new CityRequestBody()));

        //then
        verify(cityRepository, times(0)).save(any(City.class));
        verify(cityRepository, times(0)).findById(anyLong());
        verify(modelMapper, times(0)).map(any(), any());
    }


    @Test
    void givenEmptyCityList_whenFindByNameContains_thenCheckEntityNotFoundExceptionThrown() {
        //given nothing

        //when
        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by("name").ascending());
        Page<City> pageMock = mock(Page.class);
        when(cityRepository.findByNameContainsIgnoreCase(TO_AS_SEARCH_INPUT, pageRequest)).thenReturn(pageMock);
        when(pageMock.hasContent()).thenReturn(false);

        //then
        assertThrows(CityException.class, () -> cityService.getPaginatedListOfCities(TO_AS_SEARCH_INPUT, 1, 3));
        verify(cityRepository).findByNameContainsIgnoreCase(TO_AS_SEARCH_INPUT, pageRequest);
        verify(cityRepository, times(0)).findByNameContainsIgnoreCase(any());
    }

    @Test
    void givenSearchInput_whenCallfindByNameContains_thenCheckTheNumberOfResults() {
        //given
        City city1 = new City();
        city1.setId(1L);
        city1.setName(TOKYO);

        City city2 = new City();
        city2.setId(2L);
        city2.setName(HOUSTON);

        ArrayList<City> cityList = new ArrayList<>();
        cityList.add(city1);
        cityList.add(city2);

        //when
        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by("name").ascending());
        when(cityRepository.findByNameContainsIgnoreCase(TO_AS_SEARCH_INPUT, pageRequest)).thenReturn(new PageImpl<>(cityList));

        //then
        PaginatedCitiesResponse paginatedListOfCities = cityService.getPaginatedListOfCities(TO_AS_SEARCH_INPUT, 1, 3);
        assertEquals(2, paginatedListOfCities.getTotalNumberOfCities());
        assertEquals(1, paginatedListOfCities.getTotalPages());
        verify(cityRepository).findByNameContainsIgnoreCase(TO_AS_SEARCH_INPUT, pageRequest);
        verify(modelMapper, times(2)).map(any(City.class), any(CityDto.class));
    }

    @Test
    void givenCityParis_whenCreateAnotherParis_thenCheckCityExceptionThrown() {
        //given
        City city1 = new City();
        city1.setId(1L);
        city1.setName(PARIS);

        City city2 = new City();
        city2.setId(2L);
        city2.setName(PARIS);

        CityRequestBody cityRequestBody = new CityRequestBody();
        cityRequestBody.setName(PARIS);

        //when
        when(cityRepository.findById(2L)).thenReturn(Optional.empty());
        when(cityRepository.findByNameContainsIgnoreCase(PARIS)).thenReturn(new ArrayList<>() {{
            add(city1);
        }});

        //then
        assertThrows(CityException.class, () -> cityService.updateCity(2L, cityRequestBody));
        verify(cityRepository).findById(2L);
        verify(modelMapper, times(0)).map(any(), any());
    }

}

