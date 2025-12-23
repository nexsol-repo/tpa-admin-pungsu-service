package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "tb_meritz_dsf_six_rcpt_ctr_cclu_logs")
public class MeritzSixEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seqNo;

    @Column(name = "rlt_link_url_4")
    private String rltLinkUrl4;

    @Column(name = "prctr_no")
    private String prctrNo;

}
