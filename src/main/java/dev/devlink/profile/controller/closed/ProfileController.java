package dev.devlink.profile.controller.closed;

import dev.devlink.common.dto.ApiResponse;
import dev.devlink.common.file.FileConstants;
import dev.devlink.common.identity.annotation.AuthMemberId;
import dev.devlink.profile.constant.ProfileDefaults;
import dev.devlink.profile.service.ProfileService;
import dev.devlink.profile.service.dto.request.ProfileUpdateRequest;
import dev.devlink.profile.service.dto.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @AuthMemberId Long memberId,
            @PathVariable Long id
    ) {
        ProfileResponse response = profileService.getProfile(memberId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthMemberId Long memberId,
            @Validated @RequestBody ProfileUpdateRequest request
    ) {
        profileService.updateBio(memberId, request);
        return ResponseEntity.ok(ApiResponse.successEmpty());
    }

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @AuthMemberId Long memberId,
            @RequestParam MultipartFile file
    ) {
        String imageUrl = profileService.updateImage(memberId, file);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of(FileConstants.IMAGE_URL_KEY, imageUrl)));
    }
}
