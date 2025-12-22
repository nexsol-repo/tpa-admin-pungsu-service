package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PremiumAmount {

    @Column(name = "tot_ins_cost")
    private Long totalInsuranceCost;

    @Column(name = "tot_gov_ins_cost")
    private Long totalGovernmentCost;

    @Column(name = "tot_local_gov_ins_cost")
    private Long totalLocalGovernmentCost;

    @Column(name = "tot_insured_ins_cost")
    private Long totalInsuranceMyCost;

}
