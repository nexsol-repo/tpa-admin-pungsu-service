package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.storage.db.core.MeritzAreaCodeEntity;
import com.nexsol.tpa.storage.db.core.MeritzAreaCodeRepository;
import com.nexsol.tpa.storage.db.core.TotalFormMemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class DirectRegistration {

    private final MeritzAreaCodeRepository areaCodeRepository;

    /**
     * 직접 등록에 필요한 파생 데이터(지역코드, 건물급수 등)를 계산하여 엔티티에 적용합니다.
     */
    public void applyDerivedFields(TotalFormMemberEntity entity, BusinessLocationInfo location) {
        if (location == null) {
            return;
        }

        // 1. 주소 기반 City Code 검색 및 적용
        applyCityCode(entity, location.address());

        // 2. 건물 급수 계산 및 적용
        applyBuildingGrade(entity, location.mainStrctGrade(), location.roofStrctGrade(), location.mainStrctType(),
                location.roofStrctType());
    }

    private void applyCityCode(TotalFormMemberEntity entity, String address) {
        if (!StringUtils.hasText(address)) {
            return;
        }

        String[] tokens = address.trim().split("\\s+");
        if (tokens.length < 2) {
            return;
        }

        String cityText1 = tokens[0]; // 예: 부산광역시
        String cityText2 = tokens[1]; // 예: 강서구

        String cityCode = areaCodeRepository.findFirstByCityText1AndCityText2(cityText1, cityText2)
            .map(MeritzAreaCodeEntity::getCityCode)
            .orElse(null);

        // 엔티티에 지역 정보 설정 (cityCode, cityText1, cityText2)
        entity.applyCityInfo(cityCode, cityText1, cityText2);
    }

    private void applyBuildingGrade(TotalFormMemberEntity entity, String mainStrctGrade, String roofStrctGrade,
            String mainStrctType, String roofStrctType) {
        Integer bldGrade = 1; // 기본값

        if (StringUtils.hasText(mainStrctGrade) && StringUtils.hasText(roofStrctGrade)) {
            try {
                int mainVal = Integer.parseInt(mainStrctGrade);
                int roofVal = Integer.parseInt(roofStrctGrade);
                int result = mainVal * roofVal;

                // 규칙: main * roof >= 4 ? 4 : main * roof
                if (result >= 4) {
                    bldGrade = 4;
                }
                else {
                    bldGrade = Integer.valueOf(result);
                }
            }
            catch (NumberFormatException e) {
                // 숫자가 아닌 경우 기본값 유지
                bldGrade = 1;
            }
        }

        // 엔티티에 구조 타입 및 계산된 등급 설정
        entity.applyBuildingStructure(mainStrctType, roofStrctType, bldGrade);
    }

}
