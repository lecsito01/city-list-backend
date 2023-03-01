package com.example.citylist.util;

import com.example.citylist.api.City;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.util.List;

@Service
public class CsvParserService {

    public List<City> getCityEntityList(BufferedReader fileReader) {
        return new CsvToBeanBuilder<City>(fileReader)
                .withType(City.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();
    }
}
