package com.OLearning.service.termsAndCondition;

import com.OLearning.entity.TermsAndCondition;
import com.OLearning.repository.TermsAndConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import com.itextpdf.html2pdf.HtmlConverter;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Service
public class TermsAndConditionService {
    @Autowired
    private TermsAndConditionRepository termsAndConditionRepository;

    public List<TermsAndCondition> getAll() {
        return termsAndConditionRepository.findAll(Sort.by("displayOrder").ascending());
    }

    public int getMaxDisplayOrder() {
        return termsAndConditionRepository.findMaxDisplayOrder();
    }

    public void save(TermsAndCondition clause) {
        if (clause.getId() == null) {
            clause.setDisplayOrder(getMaxDisplayOrder() + 1);
        }
        termsAndConditionRepository.save(clause);
    }

    public TermsAndCondition getById(Long id) {
        return termsAndConditionRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        termsAndConditionRepository.deleteById(id);
    }

    public byte[] exportAllToPdf() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StringBuilder html = new StringBuilder();
        html.append("<h1 style='text-align:center'>BẢN ĐIỀU KHOẢN SỬ DỤNG</h1>");
        html.append("<h3 style='text-align:center'>HỆ THỐNG QUẢN LÝ KHÓA HỌC OLEARNING</h3>");
        html.append("<h4 style='font-weight:normal;color:gray;font-style:italic'>Hợp đồng Quy tắc Kinh doanh này thiết lập các nguyên tắc cơ bản, " +
                "quy định và hướng dẫn hoạt động chi phối các quy trình kinh doanh và khuôn khổ ra quyết định trong tổ chức của chúng tôi. " +
                "Các quy tắc này đảm bảo tính nhất quán, tuân thủ và hiệu suất tối ưu trên tất cả các hoạt động kinh doanh.</h4>");
        List<TermsAndCondition> terms = getAll();
        for (int i = 0; i < terms.size(); i++) {
            TermsAndCondition t = terms.get(i);
            html.append("<h3>Điều ").append(i+1).append(": ").append(t.getSectionTitle()).append("</h3>");
            html.append("<div style='margin-bottom:10px'>").append(t.getContent()).append("</div>");
            html.append("<div><b>Role:</b> ").append(t.getRoleTarget()).append("</div>");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            html.append("<div><b>Last Modified:</b> ").append(t.getUpdatedAt() != null ? t.getUpdatedAt().format(formatter) : "").append("</div>");
            html.append("<hr/>");
        }
        HtmlConverter.convertToPdf(html.toString(), baos);
        return baos.toByteArray();
    }

    public java.util.List<TermsAndCondition> getByRoleTargetOrAll(String roleTarget) {
        return termsAndConditionRepository.findByRoleTargetOrAll(roleTarget);
    }
}
