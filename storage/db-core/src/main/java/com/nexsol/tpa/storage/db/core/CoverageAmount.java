package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CoverageAmount {

    @Column(name = "ins_cost_bld")
    private Long insuranceCostBld;

    @Column(name = "ins_cost_fcl")
    private Long insuranceCostFcl;

    @Column(name = "ins_cost_mach")
    private Long insuranceCostMach;

    @Column(name = "ins_cost_inven")
    private Long insuranceCostInven;

    @Column(name = "ins_cost_shop_sign")
    private Long insuranceCostShopSign;

    @Column(name = "ins_cost_deductible")
    private Long insuranceCostDeductible;

}
