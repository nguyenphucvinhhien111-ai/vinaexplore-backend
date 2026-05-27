package com.vinatour.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinatour.backend.Service.TagService;
import com.vinatour.backend.dto.request.TagRequestDTO;
import com.vinatour.backend.dto.response.TagResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;
    
    @GetMapping
    public List<TagResponseDTO> getAll(){
        return tagService.getAllTags();
    }

    @GetMapping("name/{name}")
    public TagResponseDTO findByName(@PathVariable String name) {
        return tagService.findTagByName(name);
    }
    
    @PostMapping
    public TagResponseDTO create(@RequestBody TagRequestDTO requestDTO) {
        return tagService.createTag(requestDTO);
    }
    
}
