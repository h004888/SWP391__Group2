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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
            "và hỗ trợ trong quá trình học tập. Hãy trả lời một cách thân thiện, ngắn gọn và dễ hiểu, hữu ích và chính xác. " +
            "Nếu câu hỏi không rõ ràng hoặc không liên quan đến OLearning, hãy yêu cầu người dùng làm rõ. " +
            "Bạn không được cung cấp thông tin cá nhân của người dùng hoặc bất kỳ thông tin nhạy cảm nào. " +
            "Bạn có thể sử dụng thông tin thực tế từ hệ thống OLearning để trả lời câu hỏi. " +
            "Nếu bạn không biết câu trả lời, hãy thành thật nói rằng bạn không biết.";

    private enum ChatTopic {
        COURSE, COURSE_SEARCH, INSTRUCTOR, CATEGORY, STUDENT_COUNT, UNKNOWN
    }

    private ChatTopic detectTopic(String message) {
        String lower = message.toLowerCase();

        if (isSearchQuery(lower)) {
            return ChatTopic.COURSE_SEARCH;
        }

        if ((lower.contains("khóa học") || lower.contains("course")) &&
            (
                (lower.contains("nhiều học viên") && (lower.contains("nhất") || lower.contains("nào")))
                || lower.contains("nhiều người đăng ký")
                || lower.contains("nhiều người đăng kí")
                || lower.contains("most students")
                || lower.contains("most enrollments")
                || lower.contains("most registrations")
                || lower.contains("học viên nhất")
                || lower.contains("đăng ký nhiều nhất")
                || lower.contains("đăng kí nhiều nhất")
            )
        ) {
            return ChatTopic.COURSE;
        }

        if ((lower.contains("khóa học") || lower.contains("course")) &&
            (lower.contains("nổi bật") || lower.contains("top") || lower.contains("tiêu biểu") || lower.contains("xuất sắc") || lower.contains("best") || lower.contains("featured"))) {
            return ChatTopic.COURSE;
        }

        if (lower.contains("khóa học") || lower.contains("course")) return ChatTopic.COURSE;
        if (lower.contains("giảng viên") || lower.contains("instructor") || lower.contains("thầy cô")|| lower.contains("người dạy")) return ChatTopic.INSTRUCTOR;
        if (lower.contains("danh mục") || lower.contains("category") || lower.contains("chuyên mục")) return ChatTopic.CATEGORY;
        if (lower.contains("học viên") || lower.contains("sinh viên") || lower.contains("student")|| lower.contains("người đăng kí") ) return ChatTopic.STUDENT_COUNT;
        return ChatTopic.UNKNOWN;
    }

    private boolean isSearchQuery(String message) {
        String[] searchPatterns = {
                "tìm kiếm.*khóa học", "khóa học.*về", "có khóa học.*nào",
                "search.*course", "course.*about", "find.*course",
                "học.*về", "muốn học", "khóa.*nào", "course.*that"
        };

        for (String pattern : searchPatterns) {
            if (Pattern.compile(pattern).matcher(message).find()) {
                return true;
            }
        }
        return false;
    }

    private String extractKeyword(String message) {
        String lower = message.toLowerCase();

        String[] patterns = {
                "tìm kiếm khóa học về (.+?)(?:\\.|$|\\?|!)",
                "khóa học về (.+?)(?:\\.|$|\\?|!)",
                "có khóa học (.+?) nào",
                "muốn học (.+?)(?:\\.|$|\\?|!)",
                "học về (.+?)(?:\\.|$|\\?|!)",
                "search course about (.+?)(?:\\.|$|\\?|!)",
                "course about (.+?)(?:\\.|$|\\?|!)",
                "find course (.+?)(?:\\.|$|\\?|!)"
        };

        for (String pattern : patterns) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(lower);
            if (m.find()) {
                String keyword = m.group(1).trim();
                // Loại bỏ các từ không cần thiết
                keyword = keyword.replaceAll("\\b(nào|gì|thế|không|ko)\\b", "").trim();
                return keyword;
            }
        }

        if (lower.contains("khóa học")) {
            String[] words = lower.split("\\s+");
            StringBuilder keyword = new StringBuilder();
            boolean foundCourse = false;

            for (int i = 0; i < words.length; i++) {
                if (words[i].equals("khóa") && i + 1 < words.length && words[i + 1].equals("học")) {
                    foundCourse = true;
                    i++;
                    continue;
                }
                if (foundCourse && !isStopWord(words[i])) {
                    keyword.append(words[i]).append(" ");
                }
            }

            return keyword.toString().trim();
        }

        return "";
    }

    private boolean isStopWord(String word) {
        String[] stopWords = {"về", "nào", "gì", "thế", "không", "ko", "có", "là", "của", "trong", "và", "hoặc"};
        for (String stopWord : stopWords) {
            if (word.equals(stopWord)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ChatResponseDTO sendMessage(ChatRequestDTO request, Long userId) {
        try {
            List<ChatMessage> chatHistory = getChatHistory(request.getSessionId(), userId);

            ChatTopic topic = detectTopic(request.getMessage());
            StringBuilder contextData = new StringBuilder();

            switch (topic) {
                case COURSE_SEARCH -> {
                    String keyword = extractKeyword(request.getMessage());
                    log.info("Extracted keyword for course search: '{}'", keyword);

                    List<Course> searchResults = searchCourses(keyword);
                    contextData.append("Kết quả tìm kiếm khóa học");
                    if (!keyword.isEmpty()) {
                        contextData.append(" với từ khóa \"").append(keyword).append("\"");
                    }
                    contextData.append(":\n");

                    if (searchResults.isEmpty()) {
                        contextData.append("Không tìm thấy khóa học nào phù hợp.\n");
                    } else {
                        contextData.append("Tìm thấy ").append(searchResults.size()).append(" khóa học:\n");
                        searchResults.stream().limit(10).forEach(course -> {
                            contextData.append("- ").append(course.getTitle());
                            if (course.getDescription() != null && !course.getDescription().isEmpty()) {
                                String shortDesc = course.getDescription().length() > 100
                                        ? course.getDescription().substring(0, 100) + "..."
                                        : course.getDescription();
                                contextData.append(": ").append(shortDesc);
                            }

                            // Thêm thông tin số học viên nếu có
                            long studentCount = getStudentCountOfCourse(course.getCourseId());
                            if (studentCount > 0) {
                                contextData.append(" (").append(studentCount).append(" học viên)");
                            }
                            contextData.append("\n");
                        });
                    }
                }
                case COURSE -> {
                    String lowerMsg = request.getMessage().toLowerCase();
                    if ((lowerMsg.contains("nhiều học viên") && (lowerMsg.contains("nhất") || lowerMsg.contains("nào")))
                        || lowerMsg.contains("nhiều người đăng ký")
                        || lowerMsg.contains("nhiều người đăng kí")
                        || lowerMsg.contains("most students")
                        || lowerMsg.contains("most enrollments")
                        || lowerMsg.contains("most registrations")
                        || lowerMsg.contains("học viên nhất")
                        || lowerMsg.contains("đăng ký nhiều nhất")
                        || lowerMsg.contains("đăng kí nhiều nhất")) {
                        List<Course> topCourses = getTopCourses();
                        if (!topCourses.isEmpty()) {
                            Course mostStudentCourse = topCourses.get(0);
                            long studentCount = getStudentCountOfCourse(mostStudentCourse.getCourseId());
                            contextData.append("Khóa học có nhiều học viên/đăng ký nhất là: ")
                                .append(mostStudentCourse.getTitle());
                            if (mostStudentCourse.getDescription() != null) {
                                contextData.append(": ").append(mostStudentCourse.getDescription());
                            }
                            contextData.append(" (").append(studentCount).append(" học viên)\n");
                        } else {
                            contextData.append("Không tìm thấy dữ liệu về các khóa học.\n");
                        }
                    } else {
                        List<Course> topCourses = getTopCourses();
                        contextData.append("Top 5 khóa học nổi bật hiện có trên OLearning:\n");
                        for (Course c : topCourses) {
                            contextData.append("- ").append(c.getTitle());
                            if (c.getDescription() != null) contextData.append(": ").append(c.getDescription());
                            contextData.append("\n");
                        }
                    }
                }
                case INSTRUCTOR -> {
                    List<User> instructors = searchInstructors(""); // lấy tất cả instructor
                    contextData.append("Một số giảng viên tiêu biểu trên OLearning:\n");
                    instructors.stream().limit(5).forEach(i -> {
                        contextData.append("- ").append(i.getFullName() != null ? i.getFullName() : i.getUsername());
                        if (i.getPersonalSkill() != null) contextData.append(" (").append(i.getPersonalSkill()).append(")");
                        contextData.append("\n");
                    });
                }
                case CATEGORY -> {
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
                    String potentialKeyword = extractKeyword(request.getMessage());
                    if (!potentialKeyword.isEmpty()) {
                        List<Course> searchResults = searchCourses(potentialKeyword);
                        if (!searchResults.isEmpty()) {
                            contextData.append("Có thể bạn đang tìm hiểu về:\n");
                            searchResults.stream().limit(3).forEach(course -> {
                                contextData.append("- ").append(course.getTitle()).append("\n");
                            });
                        }
                    }
                }
            }
            
            String fullPrompt = SYSTEM_PROMPT + "\n\n";
            if (!contextData.isEmpty()) {
                fullPrompt += "Thông tin thực tế từ hệ thống:\n" + contextData + "\n";
            }
            fullPrompt += buildPromptWithContext(request.getMessage(), chatHistory);

            // Call Google AI API
            String aiResponse = callGoogleAI(fullPrompt);

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
    public List<ChatMessage> getChatHistory(String sessionId, Long userId) {
        if (userId == null) return List.of();
        return chatMessageRepository.findBySessionIdAndUser_UserIdOrderByCreatedAtAsc(sessionId, userId);
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
            GoogleAIRequestDTO request = new GoogleAIRequestDTO();
            GoogleAIRequestDTO.Content content = new GoogleAIRequestDTO.Content();
            GoogleAIRequestDTO.Part part = new GoogleAIRequestDTO.Part();
            part.setText(prompt);
            content.setParts(List.of(part));
            request.setContents(List.of(content));

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
            return "Lỗi từ API: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("Error calling Google AI API: ", e);
            return "Xin lỗi, có lỗi xảy ra khi kết nối với AI. Vui lòng thử lại sau.";
        }
    }

    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) return courseRepository.findAll();
        return courseRepository.findAll().stream()
                .filter(c -> c.getTitle() != null && c.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    public List<Course> getTopCourses() {
        return courseRepository.findAllPublishedOrderByStudentCountDesc().stream().limit(5).toList();
    }

    public List<Category> getTopCategories() {
        Pageable top5 = org.springframework.data.domain.PageRequest.of(0, 5);
        return categoryRepository.findTopCategoriesByCourseCount(top5);
    }

    public List<User> searchInstructors(String keyword) {
        Long instructorRoleId = 2L;
        return userRepository.findByUsernameContainingIgnoreCaseAndRole_RoleId(keyword, instructorRoleId, org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    public List<CourseReview> getCourseReviews(Long courseId) {
        return courseReviewRepository.findAll().stream()
                .filter(r -> r.getCourse() != null && r.getCourse().getCourseId().equals(courseId))
                .toList();
    }

    public long getStudentCountOfCourse(Long courseId) {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourse() != null && e.getCourse().getCourseId().equals(courseId))
                .count();
    }

    public long getTotalStudentCount() {
        return enrollmentRepository.countTotalStudents();
    }
}