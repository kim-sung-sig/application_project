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
public class CursorRequest<T> {

    private T cursor;  // 제네릭을 사용하여 다양한 타입을 받을 수 있도록 처리

    @NotNull(message = "페이지 사이즈는 필수입니다.")
    @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
    @Max(value = 1000, message = "페이지 사이즈는 1000 이하이어야 합니다.")
    private Long size;
}
