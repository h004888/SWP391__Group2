package com.OLearning.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Future;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Long voucherId;
    
    @NotBlank(message = "Mã voucher không được để trống")
    @Size(max = 50, message = "Mã voucher phải ít hơn 50 ký tự")
    private String code;
    
    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Phần trăm giảm giá phải từ 1% đến 100%")
    @Max(value = 100, message = "Phần trăm giảm giá phải từ 1% đến 100%")
    private Double discount;
    
    @NotNull(message = "Ngày hết hạn không được để trống")
    @Future(message = "Ngày hết hạn phải trong tương lai")
    private LocalDate expiryDate;
    
    @NotNull(message = "Số lượt sử dụng không được để trống")
    @Min(value = 1, message = "Số lượt sử dụng phải lớn hơn 0")
    private Long limitation;
    
    private Long usedCount;
    private Boolean isActive;
    private Boolean isGlobal;
    @NotNull(message = "Vui lòng chọn chế độ công khai")
    private Boolean isPublic;
    private LocalDate createdDate;
    private List<Long> courseName;
}
