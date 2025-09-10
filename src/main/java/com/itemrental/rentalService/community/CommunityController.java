package com.itemrental.rentalService.community;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

  private final CommunityService service;

  @PostMapping
  public ResponseEntity<CommunityDto> create(@RequestBody CommunityDto dto) {
    return ResponseEntity.ok(service.createPost(dto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CommunityDto> getPost(@PathVariable Long id) {
    CommunityDto dto = service.getPost(id);
    return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
  }

  @GetMapping
  public ResponseEntity<List<CommunityDto>> getAll() {
    return ResponseEntity.ok(service.getAllPosts());
  }

  @PutMapping("/{id}")
  public ResponseEntity<CommunityDto> update(@PathVariable Long id, @RequestBody CommunityDto dto) {
    CommunityDto updated = service.updatePost(id, dto);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.deletePost(id);
    return ResponseEntity.noContent().build();
  }
}
