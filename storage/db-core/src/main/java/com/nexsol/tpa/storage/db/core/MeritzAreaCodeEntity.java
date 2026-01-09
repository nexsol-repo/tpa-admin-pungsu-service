package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_meritz_sf_damage_area_code")
@Getter
@NoArgsConstructor
public class MeritzAreaCodeEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "citycode")
    private String cityCode;

    @Column(name = "city_text_1")
    private String cityText1;

    @Column(name = "city_text_2")
    private String cityText2;

    @Column(name = "city_text_3")
    private String cityText3;

}
