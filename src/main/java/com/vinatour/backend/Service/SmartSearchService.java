package com.vinatour.backend.Service;

import com.vinatour.backend.entity.Location;
import com.vinatour.backend.repository.LocationRepository;
import com.vinatour.backend.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartSearchService {

    private final ChatClient chatClient;
    private final TagRepository tagRepository;
    private final LocationRepository locationRepository;

    public List<Location> searchLocationsByPrompt(String userPrompt) {
        try {
            List<String> availableTags = tagRepository.findAll().stream()
                    .map(tag -> tag.getName())
                    .toList();

            if (availableTags.isEmpty()) {
                return List.of();
            }

            String tagsString = String.join(", ", availableTags);

            String systemMessage = "Bạn là một API trích xuất từ khóa. Nhiệm vụ duy nhất của bạn là trích xuất các Tag từ câu của người dùng.\n" +
                    "DANH SÁCH TAG HỢP LỆ DUY NHẤT: [" + tagsString + "].\n" +
                    "LUẬT NGHIÊM NGẶT CẦN TUÂN THỦ:\n" +
                    "1. CHỈ sử dụng các tag có mặt trong danh sách trên. Giữ nguyên dấu Tiếng Việt y như trong danh sách. KHÔNG bịa thêm tag mới.\n" +
                    "2. KHÔNG xuất ra bất kỳ văn bản nào khác ngoài các tag. KHÔNG giải thích, KHÔNG chào hỏi.\n" +
                    "3. KHÔNG sử dụng markdown (không dùng dấu ``` hay in đậm).\n" +
                    "4. Các tag trả về phải được ngăn cách nhau bằng đúng một dấu phẩy.\n" +
                    "Ví dụ output chuẩn: Biển, Giá rẻ, Cắm trại"; // ĐÃ SỬA CÓ DẤU

            String aiResponse = chatClient.prompt()
                    .system(systemMessage)
                    .user(userPrompt)
                    .call()
                    .content();

            log.info("Gemini trả về nguyên bản: {}", aiResponse);

            List<String> extractedTags = Arrays.stream(aiResponse.split(","))
                    .map(String::trim)
                    .map(tag -> tag.replace(".", "").replace("\"", "").replace("'", "").replace("`", ""))
                    .filter(tag -> !tag.isEmpty())
                    .map(tag -> availableTags.stream()
                            .filter(dbTag -> dbTag.equalsIgnoreCase(tag)) 
                            .findFirst()
                            .orElse(null))
                    .filter(tag -> tag != null)
                    .toList();

            log.info("Tags sau khi map với DB: {}", extractedTags);

            if (extractedTags.isEmpty()) {
                return List.of();
            }

            return locationRepository.findByTagNames(extractedTags); 

        } catch (Exception e) {
            log.error("Lỗi khi gọi Gemini Smart Search: {}", e.getMessage());
            return List.of(); 
        }
    }
}