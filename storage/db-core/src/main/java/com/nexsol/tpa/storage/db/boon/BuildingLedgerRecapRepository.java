package com.nexsol.tpa.storage.db.boon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BuildingLedgerRecapRepository extends JpaRepository<BuildingLedgerRecapEntity, String> {

    /**
     * 주소 4종 코드로 총괄표제부 조회 (단건)
     */
    @Query("SELECT r FROM BuildingLedgerRecapEntity r " + "WHERE r.sigunguCd = :sigunguCd "
            + "AND r.bjdongCd = :bjdongCd " + "AND r.bun = :bun " + "AND r.ji = :ji "
            + "ORDER BY r.mgmBldrgstPk ASC LIMIT 1")
    Optional<BuildingLedgerRecapEntity> findFirstByAddressKeys(@Param("sigunguCd") String sigunguCd,
            @Param("bjdongCd") String bjdongCd, @Param("bun") String bun, @Param("ji") String ji);

}