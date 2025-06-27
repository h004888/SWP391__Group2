package com.OLearning.service.chat.impl;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.OLearning.config.GoogleAIConfig;
import com.OLearning.dto.chat.*;
import com.OLearning.entity.ChatMessage;
import com.OLearning.entity.User;
import com.OLearning.entity.Course;
import com.OLearning.entity.Category;
import com.OLearning.entity.CourseReview;
import com.OLearning.repository.ChatMessageRepository;
import com.OLearning.repository.UserRepository;
import com.OLearning.repository.CourseRepository;
import com.OLearning.repository.CategoryRepository;
import com.OLearning.repository.EnrollmentRepository;
import com.OLearning.repository.CourseReviewRepository;
import com.OLearning.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final WebClient webClient;
    private final GoogleAIConfig googleAIConfig;
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseReviewRepository courseReviewRepository;

    private static final String SYSTEM_PROMPT = "Bạn là một trợ lý AI thông minh cho nền tảng học trực tuyến OLearning. " +
            "Bạn có thể giúp người dùng với các câu hỏi về khóa học, hướng dẫn học tập, giải thích các khái niệm, " +
            "và hỗ trợ trong quá trình học tập. Hãy trả lời một cách thân thiện,ngắn gọn và dễ hiểu, hữu ích và chính xác. " +
            "Nếu câu hỏi không rõ ràng hoặc không liên quan đến OLearning, hãy yêu cầu người dùng làm rõ. " +
            "Bạn không được cung cấp thông tin cá nhân của người dùng hoặc bất kỳ thông tin nhạy cảm nào. " +
            "Bạn có thể sử dụng thông tin thực tế từ hệ thống OLearning để trả lời câu hỏi. " +
            "Nếu bạn không biết câu trả lời, hãy thành thật nói rằng bạn không biết.";

    private enum ChatTopic {
        COURSE, INSTRUCTOR, CATEGORY, STUDENT_COUNT, UNKNOWN
    }

    private ChatTopic detectTopic(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("khóa học") || lower.contains("course")) return ChatTopic.COURSE;
        if (lower.contains("giảng viên") || lower.contains("instructor") || lower.contains("thầy cô")) return ChatTopic.INSTRUCTOR;
        if (lower.contains("danh mục") || lower.contains("category") || lower.contains("chuyên mục")) return ChatTopic.CATEGORY;
        if (lower.contains("học viên") || lower.contains("sinh viên") || lower.contains("student")) return ChatTopic.STUDENT_COUNT;
        return ChatTopic.UNKNOWN;
    }

    @Override
    public ChatResponseDTO sendMessage(ChatRequestDTO request, Long userId) {
        try {
            // Lấy lịch sử chat để tạo context
            List<ChatMessage> chatHistory = getChatHistory(request.getSessionId());

            // Phân tích chủ đề câu hỏi
            ChatTopic topic = detectTopic(request.getMessage());
            StringBuilder contextData = new StringBuilder();

            switch (topic) {
                case COURSE -> {
                    // Lấy top 5 khóa học
                    List<Course> topCourses = getTopCourses();
                    contextData.append("Top 5 khóa học nổi bật hiện có trên OLearning:\n");
                    for (Course c : topCourses) {
                        contextData.append("- ").append(c.getTitle());
                        if (c.getDescription() != null) contextData.append(": ").append(c.getDescription());
                        contextData.append("\n");
                    }
                }
                case INSTRUCTOR -> {
                    // Lấy top 5 giảng viên (theo số khóa học)
                    List<User> instructors = searchInstructors(""); // lấy tất cả instructor
                    contextData.append("Một số giảng viên tiêu biểu trên OLearning:\n");
                    instructors.stream().limit(5).forEach(i -> {
                        contextData.append("- ").append(i.getFullName() != null ? i.getFullName() : i.getUsername());
                        if (i.getPersonalSkill() != null) contextData.append(" (").append(i.getPersonalSkill()).append(")");
                        contextData.append("\n");
                    });
                }
                case CATEGORY -> {
                    // Lấy top 5 danh mục
                    List<Category> categories = getTopCategories();
                    contextData.append("Các danh mục khóa học phổ biến:\n");
                    for (Category cat : categories) {
                        contextData.append("- ").append(cat.getName()).append("\n");
                    }
                }
                case STUDENT_COUNT -> {
                    long total = getTotalStudentCount();
                    contextData.append("Hiện tại OLearning có tổng cộng khoảng ").append(total).append(" học viên đang theo học.\n");
                }
                default -> {
                    // Không thêm gì nếu không xác định được chủ đề
                }
            }

            // Tạo prompt với context
            String fullPrompt = SYSTEM_PROMPT + "\n\n";
            if (!contextData.isEmpty()) {
                fullPrompt += "Thông tin thực tế từ hệ thống:\n" + contextData + "\n";
            }
            fullPrompt += buildPromptWithContext(request.getMessage(), chatHistory);

            // Gọi Google AI API
            String aiResponse = callGoogleAI(fullPrompt);

            // Lưu tin nhắn vào database
            User user = userRepository.findById(userId).orElse(null);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(request.getSessionId());
            chatMessage.setUserMessage(request.getMessage());
            chatMessage.setAiResponse(aiResponse);
            chatMessage.setMessageType(ChatMessage.MessageType.USER);
            chatMessage.setUser(user);
            chatMessageRepository.save(chatMessage);

            return new ChatResponseDTO(
                aiResponse,
                request.getSessionId(),
                LocalDateTime.now(),
                true,
                null
            );

        } catch (Exception e) {
            log.error("Error sending message: ", e);
            return new ChatResponseDTO(
                null,
                request.getSessionId(),
                LocalDateTime.now(),
                false,
                "Có lỗi xảy ra khi xử lý tin nhắn: " + e.getMessage()
            );
        }
    }

    @Override
    public List<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @Override
    public List<ChatMessage> getUserChatHistory(Long userId) {
        return chatMessageRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void clearChatHistory(String sessionId) {
        chatMessageRepository.deleteBySessionId(sessionId);
    }

    private String buildPromptWithContext(String currentMessage, List<ChatMessage> chatHistory) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(SYSTEM_PROMPT).append("\n\n");
        
        // Thêm lịch sử chat gần đây (tối đa 10 tin nhắn)
        int historyLimit = Math.min(chatHistory.size(), 10);
        for (int i = Math.max(0, chatHistory.size() - historyLimit); i < chatHistory.size(); i++) {
            ChatMessage msg = chatHistory.get(i);
            prompt.append("User: ").append(msg.getUserMessage()).append("\n");
            prompt.append("Assistant: ").append(msg.getAiResponse()).append("\n\n");
        }
        
        prompt.append("User: ").append(currentMessage).append("\n");
        prompt.append("Assistant: ");
        
        return prompt.toString();
    }

    private String callGoogleAI(String prompt) {
        try {
            // Tạo request body
            GoogleAIRequestDTO request = new GoogleAIRequestDTO();
            GoogleAIRequestDTO.Content content = new GoogleAIRequestDTO.Content();
            GoogleAIRequestDTO.Part part = new GoogleAIRequestDTO.Part();
            part.setText(prompt);
            content.setParts(List.of(part));
            request.setContents(List.of(content));

            // Gọi API
            GoogleAIResponseDTO response = webClient.post()
                    .uri("?key=" + googleAIConfig.getApiKey())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GoogleAIResponseDTO.class)
                    .block();

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            } else {
                return "Xin lỗi, tôi không thể xử lý yêu cầu của bạn lúc này.";
            }
            
        }catch (WebClientResponseException ex) {
            // Bắt lỗi cụ thể từ WebClient
            log.error("Error calling Google AI API. Status: {}, Response Body: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            // Trả về thông báo lỗi chi tiết hơn (chỉ cho mục đích debug, có thể thay đổi sau)
            return "Lỗi từ API: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("Error calling Google AI API: ", e);
            return "Xin lỗi, có lỗi xảy ra khi kết nối với AI. Vui lòng thử lại sau.";
        }
    }

    // Lấy danh sách khóa học theo từ khóa (title)
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) return courseRepository.findAll();
        return courseRepository.findAll().stream()
            .filter(c -> c.getTitle() != null && c.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .toList();
    }

    // Lấy top 5 khóa học nhiều học viên nhất
    public List<Course> getTopCourses() {
        return courseRepository.findAllOrderByStudentCountDesc().stream().limit(5).toList();
    }

    // Lấy danh mục phổ biến nhất
    public List<Category> getTopCategories() {
        Pageable top5 = org.springframework.data.domain.PageRequest.of(0, 5);
        return categoryRepository.findTopCategoriesByCourseCount(top5);
    }

    // Lấy thông tin giảng viên theo tên (roleId = 2 là instructor)
    public List<User> searchInstructors(String keyword) {
        Long instructorRoleId = 2L;
        return userRepository.findByUsernameContainingIgnoreCaseAndRole_RoleId(keyword, instructorRoleId, org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    // Lấy đánh giá của một khóa học
    public List<CourseReview> getCourseReviews(Long courseId) {
        return courseReviewRepository.findAll().stream()
            .filter(r -> r.getCourse() != null && r.getCourse().getCourseId().equals(courseId))
            .toList();
    }

    // Lấy số lượng học viên của một khóa học
    public long getStudentCountOfCourse(Long courseId) {
        return enrollmentRepository.findAll().stream()
            .filter(e -> e.getCourse() != null && e.getCourse().getCourseId().equals(courseId))
            .count();
    }

    // Lấy số lượng học viên toàn hệ thống
    public long getTotalStudentCount() {
        return enrollmentRepository.countTotalStudents();
    }
} 