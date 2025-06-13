package com.dgu.LookIT.fitting.dto.response;

import com.dgu.LookIT.fitting.domain.BodyAnalysis;
import com.dgu.LookIT.fitting.domain.BodyType;
import com.dgu.LookIT.fitting.domain.FaceMood;
import com.dgu.LookIT.fitting.domain.FaceShape;
import com.dgu.LookIT.fitting.domain.StyleAnalysis;

public record StyleAnalysisResponse(
        BodyAnalysis bodyAnalysis,
        BodyType bodyType,
        FaceShape faceShape,
        FaceMood faceMood
) {
    public static StyleAnalysisResponse from(StyleAnalysis entity) {
        return new StyleAnalysisResponse(
                entity.getBodyAnalysis(),
                entity.getBodyType(),
                entity.getFaceShape(),
                entity.getFaceMood()
        );
    }
}
