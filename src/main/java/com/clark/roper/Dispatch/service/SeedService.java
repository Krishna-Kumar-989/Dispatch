package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.entity.Interests;
import com.clark.roper.Dispatch.entity.Languages;
import com.clark.roper.Dispatch.entity.Tag;
import com.clark.roper.Dispatch.exception.BadRequestException;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.InterestsRepository;
import com.clark.roper.Dispatch.repository.LanguagesRepository;
import com.clark.roper.Dispatch.repository.TagRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


 // Manages seed data(Languages, Interests, Tags).

@Service
public class SeedService {

  private final LanguagesRepository languagesRepository;
  private final InterestsRepository interestsRepository;
  private final TagRepository tagRepository;

  public SeedService(LanguagesRepository languagesRepository,
      InterestsRepository interestsRepository,
      TagRepository tagRepository) {
    this.languagesRepository = languagesRepository;
    this.interestsRepository = interestsRepository;
    this.tagRepository = tagRepository;
  }

  // Languages:

  @Cacheable("languages")
  public List<String> getAllLanguages() {
    return languagesRepository.findAll().stream()
        .map(Languages::getLanguage)
        .sorted()
        .collect(Collectors.toList());
  }

  @CacheEvict(value = "languages", allEntries = true)
  public String addLanguage(String name) {
    if (languagesRepository.findByLanguage(name).isPresent()) {
      throw new BadRequestException("Language '" + name + "' already exists");
    }
    Languages language = new Languages();
    language.setLanguage(name);
    languagesRepository.save(language);
    return "Language '" + name + "' added";
  }

  @CacheEvict(value = "languages", allEntries = true)
  public String removeLanguage(String name) {
    Languages language = languagesRepository.findByLanguage(name)
        .orElseThrow(() -> new ResourceNotFoundException("Language '" + name + "' not found"));
    languagesRepository.delete(language);
    return "Language '" + name + "' removed";
  }

  //Interests :

  @Cacheable("interests")
  public List<String> getAllInterests() {
    return interestsRepository.findAll().stream()
        .map(Interests::getInterest)
        .sorted()
        .collect(Collectors.toList());
  }

  @CacheEvict(value = "interests", allEntries = true)
  public String addInterest(String name) {
    if (interestsRepository.findByInterest(name).isPresent()) {
      throw new BadRequestException("Interest '" + name + "' already exists");
    }
    Interests interest = new Interests();
    interest.setInterest(name);
    interestsRepository.save(interest);
    return "Interest '" + name + "' added";
  }

  @CacheEvict(value = "interests", allEntries = true)
  public String removeInterest(String name) {
    Interests interest = interestsRepository.findByInterest(name)
        .orElseThrow(() -> new ResourceNotFoundException("Interest '" + name + "' not found"));
    interestsRepository.delete(interest);
    return "Interest '" + name + "' removed";
  }

  //Tags :

  @Cacheable("tags")
  public List<String> getAllTags() {
    return tagRepository.findAll().stream()
        .map(Tag::getName)
        .sorted()
        .collect(Collectors.toList());
  }

  @CacheEvict(value = "tags", allEntries = true)
  public String addTag(String name) {
    String upper = name.toUpperCase();
    if (tagRepository.findByName(upper).isPresent()) {
      throw new BadRequestException("Tag '" + upper + "' already exists");
    }
    Tag tag = new Tag();
    tag.setName(upper);
    tagRepository.save(tag);
    return "Tag '" + upper + "' added";
  }

  @CacheEvict(value = "tags", allEntries = true)
  public String removeTag(String name) {
    Tag tag = tagRepository.findByName(name.toUpperCase())
        .orElseThrow(() -> new ResourceNotFoundException("Tag '" + name + "' not found"));
    tagRepository.delete(tag);
    return "Tag '" + name.toUpperCase() + "' removed";
  }
}
