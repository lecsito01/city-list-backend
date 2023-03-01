package com.example.citylist.api;

import com.opencsv.bean.CsvBindByName;
import jdk.jfr.Unsigned;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cities")
public class City {

    @Id
    @Unsigned
    @SequenceGenerator(name = "citySequence", sequenceName = "city_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "citySequence")
    @Column(name = "id", nullable = false, columnDefinition = "TEXT", length = 1000)
    private Long id;

    @CsvBindByName(column = "name")
    @Column(name = "name", nullable = false)
    private String name;

    @CsvBindByName(column = "photo")
    @Column(name = "photo", nullable = false, columnDefinition = "TEXT", length = 1000)
    private String photoUrl;

}
