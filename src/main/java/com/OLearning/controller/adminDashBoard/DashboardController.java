package com.OLearning.controller.adminDashBoard;

import com.OLearning.dto.user.UserDTO;
import com.OLearning.service.courseMaintance.CourseMaintenanceService;
import com.OLearning.service.order.OrdersService;
import com.OLearning.service.user.UserService;
import com.OLearning.service.enrollment.EnrollmentService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
        LocalDate endDate = LocalDate.parse(endDateStr, formatter).plusDays(1);
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
        return ordersService.getRevenueByDateRange(startDate, endDate);
    }

    @GetMapping("/enrollments")
    @ResponseBody
    public Map<String, Long> getEnrollmentsByCategory(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter).plusDays(1);
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
        return enrollmentService.getEnrollmentsByCategoryAndDateRange(startDate, endDate);
    }

    @GetMapping("/maintenance-revenue")
    @ResponseBody
    public Map<String, Object> getMaintenanceRevenueByDateRange(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter).plusMonths(1);
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
        return courseMaintenanceService.getMaintenanceRevenueByDateRange(startDate, endDate);
    }

    @GetMapping("/export-excel-charts")
    public String exportExcelWithCharts(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            RedirectAttributes redirectAttributes,
            HttpServletResponse response,
            Model model) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter).plusDays(1);
        if (startDate.isAfter(endDate)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Start date must be before or equal to end date.");
            return "redirect:/admin";
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        response.setHeader("Content-Disposition", "attachment; filename=dashboard_report_charts_" + randomSuffix + ".xlsx");
        Map<String, Double> revenueData = ordersService.getRevenueByDateRange(startDate, endDate);
        Map<String, Long> enrollmentData = enrollmentService.getEnrollmentsByCategoryAndDateRange(startDate, endDate);
        Map<String, Object> maintenanceRevenueData = courseMaintenanceService.getMaintenanceRevenueByDateRange(startDate, endDate);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            createRevenueSheetWithChart(workbook, revenueData);
            createEnrollmentPieChartSheet(workbook, enrollmentData);
            createMaintenanceAreaChartSheet(workbook, maintenanceRevenueData);
            workbook.write(response.getOutputStream());
        }
        return null;
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createNumberStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        return style;
    }

    private CellStyle createTitleStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void createOverviewSheet(XSSFWorkbook workbook, CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle, CellStyle titleStyle) {
        XSSFSheet sheet = workbook.createSheet("Thống Kê Tổng Quan");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO THỐNG KÊ TỔNG QUAN");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        // Header
        Row header = sheet.createRow(2);
        Cell headerCell1 = header.createCell(0);
        headerCell1.setCellValue("Chỉ Số");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = header.createCell(1);
        headerCell2.setCellValue("Giá Trị");
        headerCell2.setCellStyle(headerStyle);
        
        // Data
        int rowNum = 3;
        createDataRow(sheet, rowNum++, "Tổng Số Học Viên", enrollmentService.getTotalStudents(), dataStyle, numberStyle);
        createDataRow(sheet, rowNum++, "Tổng Số Khóa Học Đăng Ký", enrollmentService.enrollmentCount(), dataStyle, numberStyle);
        createDataRow(sheet, rowNum++, "Khóa Học Đã Hoàn Thành", enrollmentService.getCompletedEnrollments(), dataStyle, numberStyle);
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createRevenueSheet(XSSFWorkbook workbook, Map<String, Double> revenueData, 
                                   CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle, CellStyle titleStyle) {
        XSSFSheet sheet = workbook.createSheet("Doanh Thu Khóa Học");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO DOANH THU KHÓA HỌC");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        // Header
        Row header = sheet.createRow(2);
        Cell headerCell1 = header.createCell(0);
        headerCell1.setCellValue("Tháng");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = header.createCell(1);
        headerCell2.setCellValue("Doanh Thu (VNĐ)");
        headerCell2.setCellStyle(headerStyle);
        
        // Data
        int rowNum = 3;
        double totalRevenue = 0;
        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(entry.getKey());
            cell1.setCellStyle(dataStyle);
            Cell cell2 = row.createCell(1);
            cell2.setCellValue(entry.getValue());
            cell2.setCellStyle(numberStyle);
            totalRevenue += entry.getValue();
        }
        
        // Tổng doanh thu
        Row totalRow = sheet.createRow(rowNum);
        Cell totalLabel = totalRow.createCell(0);
        totalLabel.setCellValue("TỔNG DOANH THU");
        totalLabel.setCellStyle(headerStyle);
        Cell totalValue = totalRow.createCell(1);
        totalValue.setCellValue(totalRevenue);
        totalValue.setCellStyle(numberStyle);
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createEnrollmentSheet(XSSFWorkbook workbook, Map<String, Long> enrollmentData,
                                      CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle, CellStyle titleStyle) {
        XSSFSheet sheet = workbook.createSheet("Học Viên Theo Danh Mục");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("PHÂN BỐ HỌC VIÊN THEO DANH MỤC");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        // Header
        Row header = sheet.createRow(2);
        Cell headerCell1 = header.createCell(0);
        headerCell1.setCellValue("Danh Mục");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = header.createCell(1);
        headerCell2.setCellValue("Số Lượng Học Viên");
        headerCell2.setCellStyle(headerStyle);
        
        // Data
        int rowNum = 3;
        long totalEnrollments = 0;
        for (Map.Entry<String, Long> entry : enrollmentData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(entry.getKey());
            cell1.setCellStyle(dataStyle);
            Cell cell2 = row.createCell(1);
            cell2.setCellValue(entry.getValue());
            cell2.setCellStyle(numberStyle);
            totalEnrollments += entry.getValue();
        }
        
        // Tổng học viên
        Row totalRow = sheet.createRow(rowNum);
        Cell totalLabel = totalRow.createCell(0);
        totalLabel.setCellValue("TỔNG HỌC VIÊN");
        totalLabel.setCellStyle(headerStyle);
        Cell totalValue = totalRow.createCell(1);
        totalValue.setCellValue(totalEnrollments);
        totalValue.setCellStyle(numberStyle);
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createMaintenanceSheet(XSSFWorkbook workbook, Map<String, Object> maintenanceData,
                                       CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle, CellStyle titleStyle) {
        XSSFSheet sheet = workbook.createSheet("Doanh Thu Bảo Trì");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO DOANH THU BẢO TRÌ");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        
        // Header
        Row header = sheet.createRow(2);
        Cell headerCell1 = header.createCell(0);
        headerCell1.setCellValue("Tháng");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = header.createCell(1);
        headerCell2.setCellValue("Doanh Thu Bảo Trì (VNĐ)");
        headerCell2.setCellStyle(headerStyle);
        
        // Data
        int rowNum = 3;
        double totalMaintenance = 0;
        for (Map.Entry<String, Object> entry : maintenanceData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(entry.getKey());
            double revenue = 0;
            if (entry.getValue() instanceof Map) {
                Object revenueObj = ((Map<?, ?>) entry.getValue()).get("revenue");
                if (revenueObj != null) {
                    try {
                        revenue = Double.parseDouble(revenueObj.toString());
                    } catch (Exception ignored) {}
                }
            }
            cell1.setCellStyle(dataStyle);
            Cell cell2 = row.createCell(1);
            cell2.setCellValue(revenue);
            cell2.setCellStyle(numberStyle);
            totalMaintenance += revenue;
        }
        
        // Tổng doanh thu bảo trì
        Row totalRow = sheet.createRow(rowNum);
        Cell totalLabel = totalRow.createCell(0);
        totalLabel.setCellValue("TỔNG DOANH THU BẢO TRÌ");
        totalLabel.setCellStyle(headerStyle);
        Cell totalValue = totalRow.createCell(1);
        totalValue.setCellValue(totalMaintenance);
        totalValue.setCellStyle(numberStyle);
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createRevenueChartSheet(XSSFWorkbook workbook, Map<String, Double> revenueData,
                                        CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle, CellStyle titleStyle) {
        XSSFSheet sheet = workbook.createSheet("Doanh Thu Với Biểu Đồ");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO DOANH THU KHÓA HỌC VỚI BIỂU ĐỒ");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        
        // Header
        Row header = sheet.createRow(2);
        Cell headerCell1 = header.createCell(0);
        headerCell1.setCellValue("Tháng");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = header.createCell(1);
        headerCell2.setCellValue("Doanh Thu (VNĐ)");
        headerCell2.setCellStyle(headerStyle);
        Cell headerCell3 = header.createCell(2);
        headerCell3.setCellValue("Biểu Đồ");
        headerCell3.setCellStyle(headerStyle);
        
        // Data với biểu đồ đơn giản
        int rowNum = 3;
        double maxRevenue = revenueData.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        
        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(entry.getKey());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(1);
            cell2.setCellValue(entry.getValue());
            cell2.setCellStyle(numberStyle);
            
            // Tạo biểu đồ đơn giản bằng text
            Cell cell3 = row.createCell(2);
            if (maxRevenue > 0) {
                int barLength = (int) ((entry.getValue() / maxRevenue) * 20);
                StringBuilder bar = new StringBuilder();
                for (int i = 0; i < barLength; i++) {
                    bar.append("█");
                }
                cell3.setCellValue(bar.toString());
            }
            cell3.setCellStyle(dataStyle);
        }
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void createEnrollmentChartSheet(XSSFWorkbook workbook, Map<String, Long> enrollmentData,
                                           CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle, CellStyle titleStyle) {
        XSSFSheet sheet = workbook.createSheet("Học Viên Với Biểu Đồ");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("PHÂN BỐ HỌC VIÊN THEO DANH MỤC VỚI BIỂU ĐỒ");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        // Header
        Row header = sheet.createRow(2);
        Cell headerCell1 = header.createCell(0);
        headerCell1.setCellValue("Danh Mục");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = header.createCell(1);
        headerCell2.setCellValue("Số Lượng");
        headerCell2.setCellStyle(headerStyle);
        Cell headerCell3 = header.createCell(2);
        headerCell3.setCellValue("Phần Trăm");
        headerCell3.setCellStyle(headerStyle);
        Cell headerCell4 = header.createCell(3);
        headerCell4.setCellValue("Biểu Đồ");
        headerCell4.setCellStyle(headerStyle);
        
        // Data với biểu đồ đơn giản
        int rowNum = 3;
        long totalEnrollments = enrollmentData.values().stream().mapToLong(Long::longValue).sum();
        
        for (Map.Entry<String, Long> entry : enrollmentData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(entry.getKey());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(1);
            cell2.setCellValue(entry.getValue());
            cell2.setCellStyle(numberStyle);
            
            // Tính phần trăm
            Cell cell3 = row.createCell(2);
            if (totalEnrollments > 0) {
                double percentage = (entry.getValue() * 100.0) / totalEnrollments;
                cell3.setCellValue(String.format("%.1f%%", percentage));
            } else {
                cell3.setCellValue("0%");
            }
            cell3.setCellStyle(dataStyle);
            
            // Tạo biểu đồ đơn giản bằng text
            Cell cell4 = row.createCell(3);
            if (totalEnrollments > 0) {
                int barLength = (int) ((entry.getValue() * 20.0) / totalEnrollments);
                StringBuilder bar = new StringBuilder();
                for (int i = 0; i < barLength; i++) {
                    bar.append("█");
                }
                cell4.setCellValue(bar.toString());
            }
            cell4.setCellStyle(dataStyle);
        }
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
    }

    private void createMaintenanceChartSheet(XSSFWorkbook workbook, Map<String, Object> maintenanceData,
                                            CellStyle headerStyle, CellStyle dataStyle, CellStyle numberStyle, CellStyle titleStyle) {
        XSSFSheet sheet = workbook.createSheet("Bảo Trì Với Biểu Đồ");
        
        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO DOANH THU BẢO TRÌ VỚI BIỂU ĐỒ");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
        
        // Header
        Row header = sheet.createRow(2);
        Cell headerCell1 = header.createCell(0);
        headerCell1.setCellValue("Tháng");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = header.createCell(1);
        headerCell2.setCellValue("Doanh Thu Bảo Trì (VNĐ)");
        headerCell2.setCellStyle(headerStyle);
        Cell headerCell3 = header.createCell(2);
        headerCell3.setCellValue("Biểu Đồ");
        headerCell3.setCellStyle(headerStyle);
        
        // Data với biểu đồ đơn giản
        int rowNum = 3;
        double maxMaintenance = 0;
        
        // Tính max value trước
        for (Object value : maintenanceData.values()) {
            try {
                double numValue = Double.parseDouble(value.toString());
                if (numValue > maxMaintenance) {
                    maxMaintenance = numValue;
                }
            } catch (NumberFormatException e) {
                // Bỏ qua nếu không phải số
            }
        }
        
        for (Map.Entry<String, Object> entry : maintenanceData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(entry.getKey());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(1);
            double revenue = 0;
            if (entry.getValue() instanceof Map) {
                Object revenueObj = ((Map<?, ?>) entry.getValue()).get("revenue");
                if (revenueObj != null) {
                    try {
                        revenue = Double.parseDouble(revenueObj.toString());
                    } catch (Exception ignored) {}
                }
            }
            cell2.setCellValue(revenue);
            cell2.setCellStyle(numberStyle);
            
            // Tạo biểu đồ đơn giản bằng text
            Cell cell3 = row.createCell(2);
            if (maxMaintenance > 0) {
                int barLength = (int) ((revenue / maxMaintenance) * 20);
                StringBuilder bar = new StringBuilder();
                for (int i = 0; i < barLength; i++) {
                    bar.append("█");
                }
                cell3.setCellValue(bar.toString());
            } else {
                cell3.setCellValue("N/A");
            }
            cell3.setCellStyle(dataStyle);
        }
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void createDataRow(XSSFSheet sheet, int rowNum, String label, long value, CellStyle dataStyle, CellStyle numberStyle) {
        Row row = sheet.createRow(rowNum);
        Cell cell1 = row.createCell(0);
        cell1.setCellValue(label);
        cell1.setCellStyle(dataStyle);
        Cell cell2 = row.createCell(1);
        cell2.setCellValue(value);
        cell2.setCellStyle(numberStyle);
    }

    // Column Chart for Revenue
    private void createRevenueSheetWithChart(XSSFWorkbook workbook, Map<String, Double> revenueData) {
        XSSFSheet sheet = workbook.createSheet("Doanh Thu Khóa Học");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tháng");
        header.createCell(1).setCellValue("Doanh Thu (VNĐ)");
        int rowNum = 1;
        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, 1, 13, 20);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Doanh Thu Theo Tháng");
        chart.setTitleOverlay(false);
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        XDDFDataSource<String> months = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, rowNum-1, 0, 0));
        XDDFNumericalDataSource<Double> revenues = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, rowNum-1, 1, 1));
        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series = data.addSeries(months, revenues);
        series.setTitle("Doanh Thu", null);
        chart.plot(data);
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    // Pie Chart for Enrollment
    private void createEnrollmentPieChartSheet(XSSFWorkbook workbook, Map<String, Long> enrollmentData) {
        XSSFSheet sheet = workbook.createSheet("Học Viên Theo Danh Mục");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Danh Mục");
        header.createCell(1).setCellValue("Số Lượng");
        int rowNum = 1;
        for (Map.Entry<String, Long> entry : enrollmentData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, 1, 13, 20);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Phân Bố Học Viên Theo Danh Mục");
        chart.setTitleOverlay(false);
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, rowNum-1, 0, 0));
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, rowNum-1, 1, 1));
        XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
        data.addSeries(categories, values);
        chart.plot(data);
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    // Area Chart for Maintenance Revenue
    private void createMaintenanceAreaChartSheet(XSSFWorkbook workbook, Map<String, Object> maintenanceData) {
        XSSFSheet sheet = workbook.createSheet("Doanh Thu Bảo Trì");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tháng");
        header.createCell(1).setCellValue("Doanh Thu Bảo Trì (VNĐ)");
        header.createCell(2).setCellValue("Số Học Viên");
        int rowNum = 1;
        for (Map.Entry<String, Object> entry : maintenanceData.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            double revenue = 0;
            if (entry.getValue() instanceof Map) {
                Object revenueObj = ((Map<?, ?>) entry.getValue()).get("revenue");
                if (revenueObj != null) {
                    try {
                        revenue = Double.parseDouble(revenueObj.toString());
                    } catch (Exception ignored) {}
                }
            }
            row.createCell(1).setCellValue(revenue);
            Object enrollmentObj = ((Map<?, ?>) entry.getValue()).get("enrollmentCount");
            long enrollment = 0;
            if (enrollmentObj != null) {
                try {
                    enrollment = Long.parseLong(enrollmentObj.toString());
                } catch (Exception ignored) {}
            }
            row.createCell(2).setCellValue(enrollment);
        }
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, 1, 13, 20);
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Doanh Thu Bảo Trì Theo Tháng");
        chart.setTitleOverlay(false);
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        XDDFDataSource<String> months = XDDFDataSourcesFactory.fromStringCellRange(sheet, new CellRangeAddress(1, rowNum-1, 0, 0));
        XDDFNumericalDataSource<Double> revenues = XDDFDataSourcesFactory.fromNumericCellRange(sheet, new CellRangeAddress(1, rowNum-1, 1, 1));
        XDDFChartData data = chart.createData(ChartTypes.AREA, bottomAxis, leftAxis);
        XDDFChartData.Series series = data.addSeries(months, revenues);
        series.setTitle("Doanh Thu Bảo Trì", null);
        chart.plot(data);
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }
}
