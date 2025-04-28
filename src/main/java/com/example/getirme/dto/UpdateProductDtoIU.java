package com.example.getirme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDtoIU {
    private String name;

    private String description;

    private Double price;

    private MultipartFile image;

    private List<SelectableContentDto> selectableContentDtoList;

}
