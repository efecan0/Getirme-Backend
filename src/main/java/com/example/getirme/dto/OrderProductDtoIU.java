package com.example.getirme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDtoIU {
    private Long size;
    private Long productId;
    //key is selectable content, value is option
    Map<Long, List<Long>> selectableContentMap;
}
