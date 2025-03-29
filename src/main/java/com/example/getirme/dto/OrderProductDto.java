package com.example.getirme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductDto {
    private Long id;

    private Long size;

    private String name;

    private List<SelectableContentDto> selectableContentDtoList;
}
