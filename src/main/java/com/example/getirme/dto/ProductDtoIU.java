package com.example.getirme.dto;

import com.example.getirme.model.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDtoIU {

    private String name;

    private String description;

    private Double price;

    private MultipartFile image;

    // key -> SelectableContent , value -> SelectableContentOption

    private Map<String , List<SelectableContentOptionDtoIU>> selectableContentOptions;

}
