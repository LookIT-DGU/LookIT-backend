package com.dgu.LookIT.fitting.service;

import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.BodyType;
import com.dgu.LookIT.fitting.dto.response.StyleRecommendationResponse;
import com.dgu.LookIT.fitting.repository.StyleAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StyleRecommendationService {

    private final StyleAnalysisRepository styleAnalysisRepository;

    public List<StyleRecommendationResponse> recommendStyles(Long userId) {
        BodyType bodyType = styleAnalysisRepository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_BODY_ANALYSIS))
                .getBodyType();

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
}
