package com.example.citylist.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/cities")
@CrossOrigin(value = "*")
public class CityController {

    private final CityService cityService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<CityDto> getCity(@NotBlank @NotNull @PathVariable Long id) {
        return ResponseEntity.ok(cityService.getCity(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<CityDto> editCity(@NotBlank @NotNull @PathVariable Long id, @RequestBody CityRequestBody cityRequestBody) {
        CityDto cityDto = cityService.updateCity(id, cityRequestBody);
        return new ResponseEntity(cityDto, HttpStatus.OK);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<PaginatedCitiesResponse> getCities(@RequestParam(value = "name", defaultValue = "", required = false) String name,
                                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                                             @RequestParam(value = "size") int size) {
        return ResponseEntity.ok(cityService.getPaginatedListOfCities(name, page, size));
    }

}
