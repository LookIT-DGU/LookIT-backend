package com.dgu.LookIT.user.dto.request;

import com.dgu.LookIT.user.domain.Gender;
import com.dgu.LookIT.user.domain.User;
import java.time.*;
public record MyInformationRequest(
        String name,
        Gender gender,
        LocalDate birthday,
        String image
){
}