package com.dgu.LookIT.util;

import com.dgu.LookIT.fitting.domain.BodyType;
import com.dgu.LookIT.fitting.domain.FaceMood;
import com.dgu.LookIT.fitting.dto.response.StyleRecommendationResponse;

import java.util.List;

public class StyleRecommendationUtil {

    public static List<StyleRecommendationResponse> getRecommendation(BodyType bodyType, FaceMood faceMood) {
        List<StyleRecommendationResponse> bodyRecommendations = getBodyRecommendations(bodyType);
        List<StyleRecommendationResponse> faceRecommendations = getFaceRecommendations(faceMood);

        // 스타일 이름만 추출해서 비교
        List<String> bodyStyleNames = bodyRecommendations.stream()
                .map(StyleRecommendationResponse::lookName)
                .toList();

        // 교집합 필터링
        List<StyleRecommendationResponse> commonRecommendations = faceRecommendations.stream()
                .filter(r -> bodyStyleNames.contains(r.lookName()))
                .toList();

        // 공통된 스타일이 있으면 반환, 없으면 얼굴 분위기 기반 스타일 반환
        return commonRecommendations.isEmpty() ? faceRecommendations : commonRecommendations;
    }


    public static List<StyleRecommendationResponse> getBodyRecommendations(BodyType bodyType) {
        return switch (bodyType) {
            case STRAIGHT -> List.of(
                    new StyleRecommendationResponse("스트레이트 체형", "균형 잡힌 비율과 안정감 있는 실루엣이 특징인 체형", ""),
                    new StyleRecommendationResponse("오피스룩", "테일러드 블레이저와 일자 팬츠, 로퍼 조합으로 깔끔한 비즈니스 캐주얼 스타일.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/office.jpg"),
                    new StyleRecommendationResponse("미니멀룩", "베이직한 티셔츠와 하이웨이스트 팬츠, 심플한 재킷. 군더더기 없는 스타일로 균형있는 체형을 살림.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/minimal.jpg"),
                    new StyleRecommendationResponse("시크룩", "슬림한 블랙 슬랙스와 화이트 셔츠, 벨트 포인트. 간결하고 세련된 느낌으로 체형에 잘 맞음.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/chic.jpg"),
                    new StyleRecommendationResponse("모던룩", "브이넥 티셔츠와 미니 스커트 조합으로 깔끔하면서도 스타일리시한 무드.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/modern.jpg"),
                    new StyleRecommendationResponse("컨템포러리룩", "심플한 실루엣의 미니 원피스와 플랫 슈즈로 현대적이고 미니멀한 느낌.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/contemporary.jpg")
            );
            case WAVE -> List.of(
                    new StyleRecommendationResponse("웨이브 체형", "섬세한 어깨라인과 긴 허리의 실루엣이 매력적인 체형", ""),
                    new StyleRecommendationResponse("프레피룩", "카라 셔츠와 체크 무늬 플리츠 스커트, 로퍼 조합. 직선적인 체형에 균형을 더해줌.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/preppy.jpg"),
                    new StyleRecommendationResponse("로맨틱룩", "부드럽고 여성스러운 스타일로, 레이스나 플라워 패턴이 있는 블라우스와 미디 스커트를 조합해 우아한 로맨틱 무드를 연출.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/romantic.jpg"),
                    new StyleRecommendationResponse("러블리룩", "프릴 블라우스와 A라인 스커트로 사랑스러운 분위기 연출.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/lovely.jpg"),
                    new StyleRecommendationResponse("코티지코어룩", "레이스와 러플 디테일이 들어간 원피스와 샌들로 빈티지하고 여성스러운 스타일.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/cottagecore.jpg"),
                    new StyleRecommendationResponse("보헤미안룩", "플로럴 패턴의 롱 드레스와 부츠, 웨이브 체형의 부드러운 실루엣을 살려줌.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/bohemian.jpg")
            );
            case NATURAL -> List.of(
                    new StyleRecommendationResponse("내추럴 체형", "직선적이며 카리스마 있는 인상을 주는 체형", ""),
                    new StyleRecommendationResponse("드뮤어룩", "단정한 셔츠와 와이드 팬츠 조합, 깔끔한 실루엣이 내추럴 체형의 직선미를 강조.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/demuir.jpg"),
                    new StyleRecommendationResponse("오버핏룩", "루즈한 니트와 와이드 팬츠로 자연스럽고 편안한 느낌을 연출.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/overfit.jpg"),
                    new StyleRecommendationResponse("젠더리스룩", "스트레이트 핏의 셔츠와 슬랙스, 중성적인 매력을 살림.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/genderless.jpg"),
                    new StyleRecommendationResponse("댄디룩", "셔츠와 베스트, 테일러드 팬츠로 클래식하면서도 날렵한 인상.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/dandy.jpg"),
                    new StyleRecommendationResponse("빈티지룩", "데님 재킷과 와이드 팬츠, 빈티지한 가방으로 캐주얼한 무드.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/vintage.jpg")
            );
        };
    }

    public static List<StyleRecommendationResponse> getFaceRecommendations(FaceMood faceMood) {
        return switch (faceMood) {
            case STRAIGHT_SKELETON -> List.of(
                    new StyleRecommendationResponse("직선 골격형", "각지고 선이 뚜렷한 얼굴형으로, 직선적인 실루엣과 구조적인 핏이 어울림, 구조적 핏과 깔끔한 라인 강, 하이넥,테일러,셔츠류 잘 받음", ""),
                    new StyleRecommendationResponse("오피스룩", "테일러드 재킷과 셔츠, 슬랙스로 깔끔하고 단정한 분위기 연출", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/office.jpg"),
                    new StyleRecommendationResponse("시크룩", "모노톤 셋업에 블랙 로퍼 또는 힐로 세련된 도시 감성", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/chic.jpg"),
                    new StyleRecommendationResponse("미니멀룩", "베이직한 티셔츠와 슬림핏 팬츠 조합. 직선 라인을 살려주는 심플한 무드", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/minimal.jpg"),
                    new StyleRecommendationResponse("프레피룩", "카라 셔츠와 니트 베스트, 체크 스커트 등 단정하고 클래식한 조합", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/preppy.jpg"),
                    new StyleRecommendationResponse("드뮤어룩", "단정한 셔츠와 와이드 팬츠 조합으로 직선 라인을 강조", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/demuir.jpg"),
                    new StyleRecommendationResponse("젠더리스룩", "성별 구분 없는 실루엣의 셔츠와 팬츠 조합", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/genderless.jpg"),
                    new StyleRecommendationResponse("댄디룩", "셔츠와 베스트, 테일러드 팬츠로 클래식하면서도 날렵한 인상.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/dandy.jpg")
            );

            case CURVED_VOLUME -> List.of(
                    new StyleRecommendationResponse("곡선 볼륨형", "부드러운 인상과 곡선적인 얼굴형으로, 곡선 디테일이 있는 옷이 어울림, 프릴,러플,플라워,레이스 추천, 둥근 카리 A라인 스커트와 궁합 좋음", ""),
                    new StyleRecommendationResponse("러블리룩", "프릴 블라우스와 A라인 스커트로 사랑스러운 무드", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/lovely.jpg"),
                    new StyleRecommendationResponse("로맨틱룩", "레이스나 플라워 패턴 블라우스와 미디 스커트로 우아하고 여성스러운 느낌", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/romantic.jpg"),
                    new StyleRecommendationResponse("코티지코어룩", "레이스, 러플이 있는 원피스와 내추럴 샌들로 빈티지하면서도 따뜻한 분위기", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/cottagecore.jpg"),
                    new StyleRecommendationResponse("보헤미안룩", "플로럴 롱 원피스와 부츠 조합, 자유롭고 감성적인 스타일", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/bohemian.jpg"),
                    new StyleRecommendationResponse("빈티지룩", "곡선미를 강조하는 부드러운 원단과 컬러의 로맨틱한 빈티지 조합", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/vintage.jpg"),
                    new StyleRecommendationResponse("오버핏룩", "루즈한 니트와 와이드 팬츠로 자연스럽고 편안한 인상", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/overfit.jpg")
            );

            case BALANCE_TYPE -> List.of(
                    new StyleRecommendationResponse("밸런스형", "직선과 곡선이 혼합된 중간형으로, 다양한 스타일을 소화할 수 있음, 실루엣에 따 직선/곡선 강조 가능", ""),
                    new StyleRecommendationResponse("모던룩", "베이직한 셔츠와 슬랙스 또는 미니스커트로 실루엣 중심의 세련된 스타일", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/modern.jpg"),
                    new StyleRecommendationResponse("컨템포러리룩", "심플한 원피스나 셋업 스타일로 미니멀하고 도시적인 느낌", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/contemporary.jpg"),
                    new StyleRecommendationResponse("미니멀룩", "베이직한 티셔츠와 하이웨이스트 팬츠, 심플한 재킷. 군더더기 없는 스타일로 균형있는 체형을 살림.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/minimal.jpg"),
                    new StyleRecommendationResponse("오버핏룩", "루즈한 니트와 와이드 팬츠로 자연스럽고 편안한 인상", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/overfit.jpg"),
                    new StyleRecommendationResponse("프레피룩", "카라 셔츠와 체크 무늬 플리츠 스커트, 로퍼 조합. 직선적인 체형에 균형을 더해줌.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/preppy.jpg"),
                    new StyleRecommendationResponse("젠더리스룩", "성별 구분 없는 실루엣의 셔츠와 팬츠 조합", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/genderless.jpg"),
                    new StyleRecommendationResponse("빈티지룩", "데님 재킷과 와이드 팬츠, 빈티지한 가방으로 캐주얼한 무드.", "https://lookit-bucket.s3.ap-northeast-2.amazonaws.com/vintage.jpg")
            );
        };
    }

}
