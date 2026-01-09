package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeritzAreaCodeRepository extends JpaRepository<MeritzAreaCodeEntity, Long> {

    Optional<MeritzAreaCodeEntity> findFirstByCityText1AndCityText2(String cityText1, String cityText2);

}
