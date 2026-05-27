package com.vinatour.backend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vinatour.backend.dto.request.TagRequestDTO;
import com.vinatour.backend.dto.response.TagResponseDTO;
import com.vinatour.backend.entity.Tag;
import com.vinatour.backend.mapper.TagMapper;
import com.vinatour.backend.repository.TagRepository;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    public List<TagResponseDTO> getAllTags(){
        return tagRepository.findAll().stream()
            .map(tagMapper::toresponseDTO).toList();
    }

    public TagResponseDTO createTag(TagRequestDTO requestDTO){
        Tag tag =tagMapper.toentity(requestDTO);
        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toresponseDTO(savedTag);
    }

    public TagResponseDTO findTagByName(String name){
        Tag tag = tagRepository.findByName(name)
            .orElseThrow(()-> new RuntimeException("Cannot find "+name));
        return tagMapper.toresponseDTO(tag);
    }
}
