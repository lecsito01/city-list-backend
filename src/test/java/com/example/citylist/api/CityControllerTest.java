package com.example.citylist.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {CityController.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CityController.class)
class CityControllerTest {

    public static final String PARIS = "Paris";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CityController cityController;

    @MockBean
    private CityService cityService;

    @Test
    void givenParis_whenGettingParis_thenExpectParisSentBack() throws Exception {
        //given
        CityDto cityDto = new CityDto();
        cityDto.setName(PARIS);

        when(cityService.getCity(anyLong())).thenReturn(cityDto);

        //when
        mockMvc.perform(get("/api/cities/{id}", 31L))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cityDto.getName())));
    }

    @Test
    void givenNewNameForCity_whenUpdatingCity_thenCheckingNewNameWasSet() throws Exception {
        //given
        CityDto cityDto = new CityDto();
        cityDto.setName("Name");
        cityDto.setPhotoUrl("photoUrl");

        when(cityService.updateCity(anyLong(), ArgumentMatchers.any())).thenReturn(cityDto);

        //when
        mockMvc.perform(put("/api/cities/{id}", 31L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"newName\", \"photoUrl\": \"photoUrl\"}"))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(cityDto.getName())))
                .andExpect(jsonPath("$.photoUrl", is(cityDto.getPhotoUrl())));
    }

    @Test
    void given2Cities_whenGettingPaginatedList_thenCheckTheseCitiesGettingBack() throws Exception {
        //given
        CityDto tokyo = new CityDto();
        tokyo.setName("Tokyo");
        tokyo.setPhotoUrl("tokyoPhotoUrl");

        CityDto paris = new CityDto();
        paris.setName("Paris");
        paris.setPhotoUrl("parisPhotoUrl");

        PaginatedCitiesResponse paginatedCitiesResponse = new PaginatedCitiesResponse();
        paginatedCitiesResponse.setTotalNumberOfCities(2);
        ArrayList<CityDto> cityDtoList = new ArrayList<>();
        cityDtoList.add(tokyo);
        cityDtoList.add(paris);
        paginatedCitiesResponse.setCityDtoList(cityDtoList);
        when(cityService.getPaginatedListOfCities("", 1, 10)).thenReturn(paginatedCitiesResponse);

        //when
        mockMvc.perform(get("/api/cities/search")
                        .param("name", "")
                        .param("page", String.valueOf(1))
                        .param("size", String.valueOf(10)))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNumberOfCities", is(2)))
                .andExpect(jsonPath("$.cityDtoList[0].name", is(tokyo.getName())))
                .andExpect(jsonPath("$.cityDtoList[0].photoUrl", is(tokyo.getPhotoUrl())))
                .andExpect(jsonPath("$.cityDtoList[1].name", is(paris.getName())))
                .andExpect(jsonPath("$.cityDtoList[1].photoUrl", is(paris.getPhotoUrl())));
    }

}

