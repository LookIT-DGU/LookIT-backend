package com.dgu.LookIT.util;

import java.util.Map;

public class StyleTipsMapper {

    public static final Map<String, String> BODY_ANALYSIS_TIPS = Map.of(
            "UPPER_EMPHASIZED_STRAIGHT", """
        • 상체강조 + 직선형
        • 상체의 부피를 분산시키는 브이넥 또는 U넥 탑 활용.
        • 어두운 색상의 심플한 상의로 시선을 절제.
        • 하체는 밝은 색상 또는 패턴 있는 스커트로 시선 유도.
        • 허리를 잡아주는 하이웨이스트 팬츠와 벨트 스타일링으로 실루엣 강조.
        """,
            "UPPER_EMPHASIZED_CURVY", """
        • 상체강조 + 곡선형
        • 바디라인을 드러내는 부드러운 니트 또는 랩 스타일 탑으로 곡선 보완.
        • 상체는 단정한 색상으로 정돈하고, 하체는 시선을 분산시킬 수 있는 패턴 활용.
        • 스트레이트 핏 하의나 부츠컷으로 하체 실루엣 커버.
        • 전체적인 실루엣을 잡아주는 긴 재킷을 매치.
        """,
            "LOWER_EMPHASIZED_STRAIGHT", """
        • 하체강조 + 직선형
        • 상체에 프릴, 셔링, 드레이프 등 입체감을 주는 디테일 탑으로 시선 유도.
        • 하체는 슬림한 무채색 실루엣으로 단정하게 마무리.
        • 상의를 하의 안에 넣는 인 스타일링으로 비율 보정.
        • 벨트로 허리 라인을 강조하고 상체에 시선 집중.
        """,
            "LOWER_EMPHASIZED_CURVY", """
        • 하체강조 + 곡선형
        • 상체는 밝은 색상의 탑이나 니트로 포인트를 주고, 곡선을 자연스럽게 드러내는 핏 선택.
        • 하체는 어두운 색상으로 정돈된 느낌을 주고, 부츠컷이나 스트레이트 핏으로 커버.
        • 랩 탑이나 페플럼 디자인 상의로 곡선미 강조.
        • 허리 강조와 다리 길이 보정을 위한 하이웨이스트 하의 추천.
        """,
            "BALANCED_STRAIGHT", """
        • 균형형 + 직선형
        • 허리를 잡아주는 핏이 강조된 상·하의 매치.
        • 상체에는 러플 또는 셔링 디테일로 입체감을 부여.
        • 하체는 하이웨이스트로 다리 길이 강조.
        • 전체적인 실루엣은 직선적이지만, 디테일로 곡선 보완.
        """,
            "BALANCED_CURVY", """
        • 균형형 + 곡선형
        • 바디라인을 자연스럽게 드러내되 과도한 타이트함은 피하기.
        • 랩 원피스나 허리선이 강조된 디자인으로 곡선미 부각.
        • 스트레이트 핏 하의로 안정감 있는 하체 연출.
        • 긴 아우터로 실루엣 정돈 및 세련된 느낌 완성.
        """
    );

    public static String getTipFor(String bodyAnalysis) {
        return BODY_ANALYSIS_TIPS.getOrDefault(bodyAnalysis, "분석 결과가 없습니다.");
    }
}
