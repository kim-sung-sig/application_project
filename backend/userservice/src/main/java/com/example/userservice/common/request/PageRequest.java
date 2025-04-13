package com.example.userservice.common.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest{

    @NotNull(message = "페이지 번호는 필수입니다.")
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    private Long page;

    @NotNull(message = "페이지 사이즈는 필수입니다.")
    @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
    @Max(value = 1000, message = "페이지 사이즈는 1000 이하이어야 합니다.")
    private Long size;

}
