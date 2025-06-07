package com.dgu.LookIT.util;

import java.util.Map;

public class StyleTipsMapper {

    public static final Map<String, String> BODY_ANALYSIS_TIPS = Map.of(
            "UPPER_EMPHASIZED", """
            • 상체강조
            • 하체에 시선을 분산시킬 수 있는 밝은색 하의 또는 패턴 있는 스커트/팬츠 착용.
            • 상체는 어두운 색 또는 심플한 디자인의 옷으로 절제된 인상을 주기.
            • 브이넥 또는 U넥으로 시선을 분산하고 상체의 부피를 덜어주는 효과.
            • 허리선을 강조한 코디로 비율을 보정.
            """,
            "LOWER_EMPHASIZED", """
            • 하체강조
            • 상체에 포인트를 주는 디테일 있는 탑이나 밝은 색 상의로 시선을 끌기.
            • 하체는 무채색 계열의 슬림한 실루엣으로 정돈된 느낌 주기.
            • A라인 스커트나 부츠컷 팬츠처럼 다리 라인을 자연스럽게 감춰주는 아이템 활용.
            • 허리선을 강조하거나 상의를 하의 안에 넣는 스타일링으로 다리 길이 보정.
            """,
            "STRAIGHT", """
            • 직선형
            • 허리 라인을 잡아주는 옷 (벨트, 셔링, 핀턱 등)으로 곡선 실루엣을 강조.
            • 러플, 프릴, 드레이프 등 볼륨감 있는 디테일을 활용해 입체감을 부여.
            • 하이웨이스트 하의로 다리 비율 강조 및 허리라인 형성.
            • 플레어 스커트나 볼륨 있는 상의로 곡선 보완.
            """,
            "CURVY", """
            • 곡선형
            • 바디라인을 자연스럽게 드러내는 핏을 선택하되 너무 타이트한 옷은 피함.
            • 랩 원피스, H라인 스커트, 부드러운 니트 등 곡선 라인을 살려주는 아이템 활용.
            • 스트레이트 핏 하의로 하체 부담을 줄이고 세련된 느낌 강조.
            • 전체적인 실루엣을 정돈해주는 긴 재킷이나 코트 활용.
            """
    );

    public static String getTipFor(String bodyAnalysis) {
        return BODY_ANALYSIS_TIPS.getOrDefault(bodyAnalysis, "");
    }
}
