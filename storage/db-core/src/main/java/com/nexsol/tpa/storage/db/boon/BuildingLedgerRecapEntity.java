package com.nexsol.tpa.storage.db.boon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_br_recap_title_info") //
@Getter
@NoArgsConstructor
public class BuildingLedgerRecapEntity {

    @Id
    @Column(name = "mgm_bldrgst_pk")
    private String mgmBldrgstPk;

    // 주소 키 (검색 조건) [cite: 201]
    @Column(name = "sigungu_cd")
    private String sigunguCd;

    @Column(name = "bjdong_cd")
    private String bjdongCd;

    @Column(name = "bun")
    private String bun;

    @Column(name = "ji")
    private String ji;

    // 화면 표시 데이터
    @Column(name = "bld_nm")
    private String buildingName; // 건물명

    @Column(name = "tot_area")
    private Double totArea; // 연면적

    @Column(name = "use_apr_day")
    private String useAprDay; // 사용승인일

}