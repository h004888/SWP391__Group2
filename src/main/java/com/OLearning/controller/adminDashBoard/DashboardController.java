package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import com.OLearning.service.order.OrdersService;
import com.OLearning.service.user.UserService;
import com.OLearning.service.enrollment.EnrollmentService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
public class DashboardController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private CourseMaintenanceService courseMaintenanceService;

    @GetMapping()
    public String getAdminDashboardPAge(Model model) {
        model.addAttribute("fragmentContent", "adminDashBoard/fragments/content :: contentMain");
        Map<String, Double> revenuePerMonth = ordersService.getRevenuePerMonth();
        model.addAttribute("revenuePerMonth", revenuePerMonth);
        
        // Get initial enrollment data for current year
        LocalDate now = LocalDate.now();
        LocalDate firstDay = LocalDate.of(now.getYear(), 1, 1);
        LocalDate lastDay = LocalDate.of(now.getYear(), 12, 31);
        Map<String, Long> enrollmentsByCategory = enrollmentService.getEnrollmentsByCategoryAndDateRange(firstDay, lastDay);
        model.addAttribute("enrollmentsByCategory", enrollmentsByCategory);
        
        model.addAttribute("totalEnrollment", enrollmentService.enrollmentCount());
        model.addAttribute("totalStudents", enrollmentService.getTotalStudents());
        model.addAttribute("completedEnrollments", enrollmentService.getCompletedEnrollments());
        
        // Get top instructors sorted by number of courses
        List<UserDTO> topInstructors = userService.getTopInstructorsByCourseCount(5);
        model.addAttribute("topInstructors", topInstructors);

        // Get maintenance revenue for current year
        Map<String, Object> maintenanceRevenue = courseMaintenanceService.getMaintenanceRevenueByDateRange(firstDay, lastDay);
        model.addAttribute("maintenanceRevenue", maintenanceRevenue);
        
        return "adminDashBoard/index";
    }

    @GetMapping("/revenue")
    @ResponseBody
    public Map<String, Double> getRevenueByDateRange(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        return ordersService.getRevenueByDateRange(startDate, endDate);
    }

    @GetMapping("/enrollments")
    @ResponseBody
    public Map<String, Long> getEnrollmentsByCategory(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        return enrollmentService.getEnrollmentsByCategoryAndDateRange(startDate, endDate);
    }

    @GetMapping("/maintenance-revenue")
    @ResponseBody
    public Map<String, Object> getMaintenanceRevenueByDateRange(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        return courseMaintenanceService.getMaintenanceRevenueByDateRange(startDate, endDate);
    }

    @GetMapping("/export-all")
    public void exportAllToExcel(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            HttpServletResponse response) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        
        Map<String, Double> revenueData = ordersService.getRevenueByDateRange(startDate, endDate);
        Map<String, Long> enrollmentData = enrollmentService.getEnrollmentsByCategoryAndDateRange(startDate, endDate);
        Map<String, Object> maintenanceRevenueData = courseMaintenanceService.getMaintenanceRevenueByDateRange(startDate, endDate);
        
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=dashboard_report.xlsx");
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Sheet 1: Thống kê tổng quan
            XSSFSheet overviewSheet = workbook.createSheet("Thống Kê Tổng Quan");
            Row overviewHeader = overviewSheet.createRow(0);
            overviewHeader.createCell(0).setCellValue("Chỉ Số");
            overviewHeader.createCell(1).setCellValue("Giá Trị");
            
            int overviewRow = 1;
            overviewSheet.createRow(overviewRow++).createCell(0).setCellValue("Tổng Số Học Viên");
            overviewSheet.getRow(overviewRow-1).createCell(1).setCellValue(enrollmentService.getTotalStudents());
            
            overviewSheet.createRow(overviewRow++).createCell(0).setCellValue("Tổng Số Khóa Học Đăng Ký");
            overviewSheet.getRow(overviewRow-1).createCell(1).setCellValue(enrollmentService.enrollmentCount());
            
            overviewSheet.createRow(overviewRow++).createCell(0).setCellValue("Khóa Học Đã Hoàn Thành");
            overviewSheet.getRow(overviewRow-1).createCell(1).setCellValue(enrollmentService.getCompletedEnrollments());
            
            // Sheet 2: Doanh thu theo tháng
            XSSFSheet revenueSheet = workbook.createSheet("Doanh Thu Khóa Học");
            Row revenueHeader = revenueSheet.createRow(0);
            revenueHeader.createCell(0).setCellValue("Tháng");
            revenueHeader.createCell(1).setCellValue("Doanh Thu");
            
            int revenueRow = 1;
            for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
                Row row = revenueSheet.createRow(revenueRow++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }
            
            // Sheet 3: Số lượng học viên theo danh mục
            XSSFSheet enrollmentSheet = workbook.createSheet("Học Viên Theo Danh Mục");
            Row enrollmentHeader = enrollmentSheet.createRow(0);
            enrollmentHeader.createCell(0).setCellValue("Danh Mục");
            enrollmentHeader.createCell(1).setCellValue("Số Lượng Học Viên");
            
            int enrollmentRow = 1;
            for (Map.Entry<String, Long> entry : enrollmentData.entrySet()) {
                Row row = enrollmentSheet.createRow(enrollmentRow++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }

            // Sheet 4: Doanh thu bảo trì
            XSSFSheet maintenanceSheet = workbook.createSheet("Doanh Thu Bảo Trì");
            Row maintenanceHeader = maintenanceSheet.createRow(0);
            maintenanceHeader.createCell(0).setCellValue("Tháng");
            maintenanceHeader.createCell(1).setCellValue("Doanh Thu Bảo Trì");
            
            int maintenanceRow = 1;
            for (Map.Entry<String, Object> entry : maintenanceRevenueData.entrySet()) {
                Row row = maintenanceSheet.createRow(maintenanceRow++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue().toString());
            }
            
            // Auto-size columns for all sheets
            for (int i = 0; i < 2; i++) {
                overviewSheet.autoSizeColumn(i);
                revenueSheet.autoSizeColumn(i);
                enrollmentSheet.autoSizeColumn(i);
                maintenanceSheet.autoSizeColumn(i);
            }
            
            workbook.write(response.getOutputStream());
        }
    }
}
