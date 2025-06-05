package com.dgu.LookIT.auth.dto.request;

import com.dgu.LookIT.user.domain.Gender;
import com.dgu.LookIT.user.domain.User;

import java.time.LocalDate;

public record SignUpRequest(
        String name,
        Gender gender,
        LocalDate birthday,
        String image
) {
}
