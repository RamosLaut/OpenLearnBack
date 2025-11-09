package com.openlearn.OpenLearn.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private String id;
    private String email;
    private String resetCode;
    private String resetCodeExpiry;
}
