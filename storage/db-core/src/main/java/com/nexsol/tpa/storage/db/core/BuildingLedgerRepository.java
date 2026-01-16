package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuildingLedgerRepository extends JpaRepository<BuildingLedgerEntity, String> {

    /**
     * 시군구, 법정동, 번, 지 코드로 대지구분코드(platGbCd)를 단건 조회 (PNU 생성용)
     */
    @Query("SELECT b.platGbCd FROM BuildingLedgerEntity b " + "WHERE b.sigunguCd = :sigunguCd "
            + "AND b.bjdongCd = :bjdongCd " + "AND b.bun = :bun " + "AND b.ji = :ji "
            + "ORDER BY b.mgmBldrgstPk DESC LIMIT 1")
    Optional<String> findPlatGbCd(@Param("sigunguCd") String sigunguCd, @Param("bjdongCd") String bjdongCd,
            @Param("bun") String bun, @Param("ji") String ji);

    /**
     * [추가] 상세 주소 4종으로 해당 대지의 모든 건축물 대장 정보 조회 (하나의 대지에 여러 동의 건물이 있을 수 있음)
     */
    @Query("SELECT b FROM BuildingLedgerEntity b " + "WHERE b.sigunguCd = :sigunguCd " + "AND b.bjdongCd = :bjdongCd "
            + "AND b.bun = :bun " + "AND b.ji = :ji " + "ORDER BY b.mgmBldrgstPk ASC")
    List<BuildingLedgerEntity> findAllByAddressKeys(@Param("sigunguCd") String sigunguCd,
            @Param("bjdongCd") String bjdongCd, @Param("bun") String bun, @Param("ji") String ji);

}