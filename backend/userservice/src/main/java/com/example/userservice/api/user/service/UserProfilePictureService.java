package com.example.userservice.api.user.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.userservice.api.user.repository.UserProfilePictureRepository;
import com.example.userservice.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfilePictureService {

    private final UserRepository userRepository;
    private final UserProfilePictureRepository userProfilePictureRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final String CACHE_PREFIX = "profileImages::";

    // public ResponseEntity<Resource> getProfileResource(UUID profileId) throws Exception {
    //     String cacheKey = CACHE_PREFIX + profileId;

    //     // 캐시 적용
    //     String cachedFilePath = redisTemplate.opsForValue().get(cacheKey);
    //     if (cachedFilePath != null) {
    //         log.info("profileId => {} .. 캐시에서 가져온 값 : {}", profileId, cachedFilePath);
    //         return buildResponse(cachedFilePath);
    //     }

    //     // 1. DB에서 프로필 파일 경로 조회
    //     FilePath profilePath = userProfilePictureRepository.findFilePathById(profileId)
    //             .orElseThrow(() -> new FileNotFoundException("해당 파일이 존재하지 않습니다."));

    //     String filePath = profilePath.filePath();

    //     // 2. Redis에 경로 저장 (TTL 30분 설정)
    //     redisTemplate.opsForValue().set(cacheKey, filePath, Duration.ofMinutes(30));

    //     // 3. 리턴
    //     return buildResponse(filePath);
    // }

    // private ResponseEntity<Resource> buildResponse(String filePath) throws Exception {
    //     Path path = Paths.get(filePath);
    //     UrlResource resource = new UrlResource(path.toUri());
    //     if (!resource.exists() || !resource.isReadable()) {
    //         throw new FileNotFoundException("해당 파일이 존재하지 않습니다.");
    //     }

    //     return ResponseEntity.ok()
    //             .contentType(FileUtil.getContentType(filePath))
    //             .body(resource);
    // }


    // 프로필 사진 등록
    public void createProfilePicture(Long userId, MultipartFile file) {

    }
}
