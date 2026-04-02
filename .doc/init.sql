create table db_tpa_dev.total_formmembers
(
    id                         int auto_increment
        primary key,
    refer_idx                  varchar(20)                              not null comment '새로운 키값',
    businessnumber             varchar(20)                              null comment '사업자번호',
    company                    varchar(50)                              null comment '상호명',
    phone                      varchar(30)                              null comment '가입자 휴대폰번호',
    zonecode                   varchar(8)                               null comment '우편번호',
    address                    varchar(100)                             null comment '도로명주소',
    oldaddress                 varchar(50)                              null comment '지번주소',
    addressdetail              varchar(500)                             null comment '상세주소',
    pnu                        char(19)                                 null comment '필지고유번호',
    sido                       varchar(20)                              null comment '시/도',
    citycode                   varchar(6)                               null comment '풍수해 시군구코드',
    city_text_1                varchar(10)                              null,
    city_text_2                varchar(10)                              null,
    structure                  varchar(50)                              null comment '영업장 건물구조',
    etcstructure               varchar(50)                              null comment '영업장 건물구조(상세)',
    tenant                     varchar(10)                              null comment '임차 여부(임차자, 소유자)',
    underground                varchar(10)                              null comment '지하소재 여부(예, 아니오)',
    floor                      varchar(30)                              null comment '건물 전체 층수',
    subfloor                   varchar(30)                              null comment '건물 시작층',
    endsubfloor                varchar(30)                              null comment '건물 끝층',
    biztype                    varchar(20)                              null comment '소상공인 구분(소상인(일반), 소공인(공장))',
    bizcategory                varchar(50)                              null comment '업종',
    ins_target                 varchar(10)                              null comment '보험가입대상(일반, 공장)',
    checkone                   varchar(10)                              null comment '소상공인 체크리스트1',
    checktwo                   varchar(10)                              null comment '소상공인 체크리스트2',
    checkthr                   varchar(10)                              null comment '소상공인 체크리스트3',
    checkterms                 varchar(10)                              null comment '선택 동의사항',
    check_support_agree        varchar(5)                               null comment '재난지원금 중복 제한 동의',
    check_digisig_agree        varchar(5)                               null comment '전자서명 동의',
    sign                       varchar(50)                              null comment '대표자명',
    name                       varchar(20)                              null,
    time                       varchar(20)                              null comment '접수일자(YYYY.MM.DD)',
    businessfile               varchar(150)                             null comment '사업자등록증',
    smallbusiness              varchar(150)                             null comment '소상공인확인서',
    insurancefile              varchar(150)                             null comment '보험가입신청서',
    etc                        varchar(50)                              null,
    meritz_flooding_log        varchar(20)                              null comment '메리츠침수지역데이터기반 데이터수정로그',
    signfile                   varchar(50)                              null comment '대표자 서명 파일',
    status                     varchar(20)                              null,
    event_div                  varchar(10)                              null comment '가입 대상 지역',
    event_num                  varchar(10)                              null comment '가입 차수 정보',
    account                    varchar(20)                              null comment '제휴사',
    path                       varchar(30)                              null comment '채널',
    ins_com                    varchar(20)                              null comment '보험사',
    ins_div                    varchar(20)                              null comment '보험 상품명',
    entry_div                  varchar(20)                              null comment '접수방식(온라인/오프라인)',
    ins_sdate                  datetime                                 not null comment '보험 시작일',
    ins_start_hm               varchar(10)                              null comment '보험 시작 시분',
    ins_edate                  datetime                                 not null comment '보험 종료일',
    ins_end_hm                 varchar(10)                              null comment '보험 종료 시분',
    ins_number                 varchar(30)                              null comment '증권번호',
    join_ck                    char         default 'N'                 null comment '계약 진행상태( Y:가입완료,C:임의해지,F:가입오류)',
    result_seq_no              int unsigned default 0                   null comment '가입유효 테이블 seq_no',
    mgm_bldrgst_pk             varchar(50)                              null comment '건축물대장 표제부 PK',
    regstr_gb_cd_nm            varchar(10)                              null comment '건축물대장구분',
    KODATA_CK                  varchar(1)   default 'N'                 null comment '사업자정보 유무체크(I: 기가입자 Y:코데이터 K:코데이터법인 Z:제로페이 F:글로벌핀테크 N:수동입력)',
    referer                    varchar(50)                              null comment '가입한 url 데이터',
    comment                    varchar(500)                             null,
    business_item_cd           varchar(6)                               null comment '업태 코드',
    business_item_nm           varchar(100)                             null comment '업태 코드명',
    bizcategory_change_yn      varchar(1)                               null comment '업종 변경 유무',
    biznum_status_ck           varchar(1)   default 'N'                 null comment '사업자등록 상태조회(휴폐업조회) 여부(N:조회X Y:계속사업자 F:휴폐업자)',
    join_auto_type             varchar(2)   default 'M'                 null comment '자동 가입 구분(A:자동(보험사 API), M:수동)',
    cert_logs_seq_no           int          default 0                   null comment '휴대폰 본인인증 로그 seq_no',
    pay_yn                     varchar(1)   default 'N'                 null comment '결제 여부(Y: 예(유료), N: 아니오(무료))',
    pay_method                 varchar(40)                              null comment '결제수단(CARD:신용카드, BANK:계좌이체, VBANK : 가상계좌, DBANK : 무통장입금)',
    pay_status                 varchar(1)                               null comment '결제 상태(N:결제전, Y:결제완료,C:환불완료)',
    pay_dt                     datetime                                 null comment '결제일시',
    pay_logs_seq_no            int          default 0                   null comment '결제 로그 seq_no',
    biz_member_seq_no          int          default 0                   null comment '사업자정보 통합테이블(가입보험) seq_no',
    rcpt_seq_no                int          default 0                   null comment '현금영수증 발급 테이블 seq_no',
    biz_no_type                varchar(1)                               null comment '사업자 구분(P:개인사업자, C:법인사업자, N:사업자아님)',
    insured_nm                 varchar(200)                             null comment '피보험자 성명(대표자명)',
    insured_rel_cd             int                                      null comment '피보험자와의 관계 코드(1:대표자, 2:대리인)',
    insured_rr_no              varchar(14)                              null comment '피보험자 주민번호(법인:앞 7자리, 개인)',
    insured_email              varchar(100)                             null comment '피보험자 이메일',
    ins_cost_bld               bigint       default 0                   null comment '건물 가입금액',
    ins_cost_fcl               bigint       default 0                   null comment '시설 가입금액',
    ins_cost_item              bigint       default 0                   null comment '집기비품 가입금액',
    ins_cost_mach              bigint       default 0                   null comment '기계 가입금액',
    ins_cost_inven             bigint       default 0                   null comment '재고자산 가입금액',
    ins_cost_deductible        bigint       default 0                   null comment '자기부담금',
    ins_cost_shop_sign         bigint       default 0                   null comment '야외간판 특약 가입금액',
    apply_cost                 bigint       default 0                   null comment '적용 보험료(결제 보험료)',
    tot_ins_cost               bigint       default 0                   null comment '총 보험료',
    tot_gov_ins_cost           bigint       default 0                   null comment '정부부담 보험료',
    tot_local_gov_ins_cost     bigint       default 0                   null comment '지방자치단체부담 보험료',
    tot_account_ins_cost       bigint       default 0                   null comment '제휴사부담 보험료',
    tot_insured_ins_cost       bigint       default 0                   null comment '개인부담 보험료',
    prctr_no                   varchar(100)                             null comment '가계약번호(청약번호)',
    prem_cmpt_logs_seq_no      int          default 0                   null comment '보험료산출 로그 seq_no',
    join_renew                 int          default 0                   null comment '보험갱신여부(0:첫가입, 1~ :n차수)(넥솔 기준)',
    bf_id                      int          default 0                   null comment '갱신전 id',
    bf_refer_idx               varchar(50)                              null comment '갱신전 refer_idx',
    bf_ins_stock_no            varchar(100)                             null comment '갱신전 증권번호(갱신계약 청약시 갱신 이전 증권번호)',
    terms_agree                varchar(100)                             null comment '약관동의내용(순차적으로 1/2 중 입력)',
    mgm_bldrgst_pk_recap_title varchar(33)                              null comment '건축물대장 총괄표제부 PK',
    mgm_bldrgst_pk_expos_list  mediumtext                               null comment '건축물대장 전유부 PK (호별로 구분)',
    bld_nm                     varchar(100)                             null comment '건물명(표제부)',
    sigungu_cd                 varchar(5)                               null comment '시군구코드(표제부)',
    bjdong_cd                  varchar(5)                               null comment '법정동코드(표제부)',
    plat_gb_cd                 varchar(1)                               null comment '대지구분코드(0:대지 1:산 2:블록)(표제부)',
    bun                        varchar(4)                               null comment '번(표제부)',
    ji                         varchar(4)                               null comment '지(표제부)',
    dong_nm                    varchar(100)                             null comment '동명(표제부)',
    using_dong_nm_list         mediumtext                               null comment '사업장 사용동 명(건물 전체 선택)',
    using_flr_index_list       mediumtext                               null comment '사업장 사용층 index',
    using_flr_nm_list          mediumtext                               null comment '사업장 사용층 명',
    using_ho_nm_list           mediumtext                               null comment '사업장 사용호 명',
    using_area                 decimal(19, 9)                           null comment '사업장 연면적(㎡)',
    using_area_change_yn       varchar(1)                               null comment '사업장 연면적 수정여부(Y/N)',
    grnd_flr_cnt               mediumint(5)                             null comment '건물 지상 층수(표제부)',
    ugrnd_flr_cnt              mediumint(5)                             null comment '건물 지하층수(표제부)',
    main_strct_type            varchar(20)                              null comment '주구조 종류',
    roof_strct_type            varchar(20)                              null comment '지붕구조 종류',
    wall_strct_type            varchar(20)                              null comment '외벽구조 종류',
    bld_grade                  int          default 0                   null comment '건물급수',
    use_apr_day                varchar(30)                              null comment '사용승인일(YYYYMMDD)',
    rserthqk_dsgn_apply_yn     varchar(1)                               null comment '내진설계여부(Y/N)',
    accident_yn                varchar(1)   default 'N'                 null comment '사고접수여부(N:신청안함, Y:신청)',
    tm_yn                      varchar(1)                               null comment '전통시장 소상공인 여부(Y/N)',
    dsf_area_yn                varchar(1)                               null comment '최근 풍수해 피해지역 여부',
    ground_floor_yn            varchar(1)                               null comment '사업장 지하층 or 1층 여부(Y: 지하층,1층, N: 2층이상)',
    ground_floor_cd            varchar(1)                               null comment '사업장 지하소재여부 코드(0: 지하, 1: 지상(1층), 2: 지상(그외))',
    br_refer_json              longtext                                 null comment '건축물대장 참고 데이터',
    refer_json                 longtext                                 null comment '참고 데이터',
    rec_ins_sdate              datetime                                 null comment '보험 시작일(기록용)',
    rec_ins_start_hm           varchar(10)                              null comment '보험 시작 시분(기록용)',
    rec_ins_edate              datetime                                 null comment '보험 종료일(기록용)',
    rec_ins_end_hm             varchar(10)                              null comment '보험 종료 시분(기록용)',
    disbursedAt                datetime                                 null comment '수납일시(보험사)',
    createdAt                  datetime     default current_timestamp() not null comment '생성일시',
    korea_createdAt            datetime     default current_timestamp() not null comment '생성일시(KST)',
    updatedAt                  datetime     default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deletedAt                  datetime                                 null comment '삭제일시',
    contract_name              varchar(50)                              null comment '계약자(상호명)',
    contract_business_number   varchar(100)                             null comment '계약자 사업자 번호',
    contract_address           varchar(255)                             null comment '계약자 주소',
    constraint bn_company_ed_en_acc_ic_id_is_ie_jc
        unique (businessnumber, company, event_div, event_num, account, ins_com, ins_div, ins_sdate, ins_edate,
                join_ck),
    constraint refer_idx_UNIQUE
        unique (refer_idx)
)
    comment '풍수해6 가입신청 정보 테이블';

create index bf_refer_idx
    on db_tpa_dev.total_formmembers (bf_refer_idx);

create index deleted_at
    on db_tpa_dev.total_formmembers (deletedAt);

create index id__deleted_at
    on db_tpa_dev.total_formmembers (id, deletedAt);

create index ins_number
    on db_tpa_dev.total_formmembers (ins_number);

create index phone
    on db_tpa_dev.total_formmembers (phone);

create index prctr_no
    on db_tpa_dev.total_formmembers (prctr_no);

create index tf_bn
    on db_tpa_dev.total_formmembers (businessnumber);

create index tf_ic
    on db_tpa_dev.total_formmembers (ins_com);

-- 풍수해 결제취소(환불) 테이블
create table db_tpa_dev.pungsu_payment_cancel
(
    id            bigint auto_increment primary key,
    contract_id   int          not null comment '계약 ID (total_formmembers.id)',
    refund_amount bigint       not null comment '환불 금액',
    refund_method varchar(40)  null comment '환불 방법 (카드취소, 계좌환불 등)',
    refund_dt     datetime     not null comment '환불 일시',
    refund_reason varchar(500) null comment '환불 사유',
    created_at    datetime default current_timestamp() not null comment '생성일시',
    updated_at    datetime default current_timestamp() not null on update current_timestamp() comment '수정일시'
) comment '풍수해 결제취소(환불) 테이블';

create index idx_pungsu_payment_cancel_contract_id
    on db_tpa_dev.pungsu_payment_cancel (contract_id);
